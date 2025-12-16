package com.example.stepdrink.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepdrink.data.local.database.AppDatabase
import com.example.stepdrink.data.local.entity.StepRecord
import com.example.stepdrink.data.repository.StepRepository
import com.example.stepdrink.sensor.StepCounterManager
import com.example.stepdrink.data.local.PreferencesManager
import com.example.stepdrink.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StepRepository = StepRepository(
        AppDatabase.getDatabase(application).stepDao()
    )
    val stepCounterManager: StepCounterManager = StepCounterManager(application)

    private val preferencesManager = PreferencesManager(application)

    val dailyGoal: StateFlow<Int> = preferencesManager.stepGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000)

    val todaySteps: StateFlow<StepRecord?> = repository.getStepsByDate(DateUtils.getCurrentDate())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val last7DaysSteps: StateFlow<List<StepRecord>> = repository.getLast7DaysSteps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe sensor dan update database
        viewModelScope.launch {
            stepCounterManager.totalSteps.collect { steps ->
                if (steps > 0) {
                    updateTodaySteps(steps)
                }
            }
        }
    }

    private fun updateTodaySteps(steps: Int) {
        viewModelScope.launch {
            repository.insertOrUpdateSteps(DateUtils.getCurrentDate(), steps)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopTracking()
    }
}
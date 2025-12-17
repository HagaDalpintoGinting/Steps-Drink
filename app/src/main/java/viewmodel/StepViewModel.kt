package com.example.stepdrink.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepdrink.data.local.PreferencesManager
import com.example.stepdrink.data.local.database.AppDatabase
import com.example.stepdrink.data.local.entity.StepRecord
import com.example.stepdrink.data.repository.StepRepository
import com.example.stepdrink.sensor.StepCounterManager
import com.example.stepdrink.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {

    // PERBAIKAN: Initialize repository dulu di init block
    private val repository: StepRepository
    val stepCounterManager: StepCounterManager
    private val preferencesManager = PreferencesManager(application)

    val dailyGoal: StateFlow<Int>
    val userWeight: StateFlow<Int>
    val todaySteps: StateFlow<StepRecord?>
    val last7DaysSteps: StateFlow<List<StepRecord>>

    init {
        // Initialize database & repository
        val database = AppDatabase.getDatabase(application)
        repository = StepRepository(database.stepDao())
        stepCounterManager = StepCounterManager(application)

        // Initialize flows
        dailyGoal = preferencesManager.stepGoal
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000)

        userWeight = preferencesManager.userWeight
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 70)

        todaySteps = repository.getStepsByDate(DateUtils.getCurrentDate())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        last7DaysSteps = repository.getLast7DaysSteps()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun getProgressPercentage(): Float {
        val today = todaySteps.value?.steps ?: 0
        return (today.toFloat() / dailyGoal.value.toFloat()).coerceIn(0f, 1f)
    }

    // Function untuk hitung kalori
    fun calculateCalories(steps: Int, weight: Int): Int {
        // Rumus: (Steps × Weight × 0.57) / 1000
        // Contoh: 10000 steps × 70kg = 399 kalori
        return ((steps * weight * 0.57) / 1000).toInt()
    }

    // Get kalori untuk hari ini
    fun getTodayCalories(): Int {
        val steps = todaySteps.value?.steps ?: 0
        val weight = userWeight.value
        return calculateCalories(steps, weight)
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopTracking()
    }
}
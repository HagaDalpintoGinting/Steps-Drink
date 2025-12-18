package com.example.stepdrink.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepdrink.data.local.PreferencesManager
import com.example.stepdrink.data.local.database.AppDatabase
import com.example.stepdrink.data.local.entity.StepRecord
import com.example.stepdrink.data.repository.StepRepository
import com.example.stepdrink.sensor.ImprovedStepManager
import com.example.stepdrink.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StepRepository
    val improvedStepManager: ImprovedStepManager
    private val preferencesManager = PreferencesManager(application)

    val dailyGoal: StateFlow<Int>
    val userWeight: StateFlow<Int>
    val todaySteps: StateFlow<StepRecord?>
    val last7DaysSteps: StateFlow<List<StepRecord>>

    companion object {
        private const val TAG = "StepViewModel"
    }

    init {
        Log.d(TAG, "=== VIEWMODEL INIT ===")

        // Initialize database & repository
        val database = AppDatabase.getDatabase(application)
        repository = StepRepository(database.stepDao())
        improvedStepManager = ImprovedStepManager(application)

        Log.d(TAG, "Sensor available: ${improvedStepManager.isSensorAvailable()}")

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
            improvedStepManager.totalSteps.collect { steps ->
                Log.d(TAG, "Steps updated: $steps")
                if (steps > 0) {
                    updateTodaySteps(steps)
                }
            }
        }

        // Log sensor status
        viewModelScope.launch {
            improvedStepManager.sensorStatus.collect { status ->
                Log.d(TAG, "Sensor status: $status")
            }
        }
    }

    private fun updateTodaySteps(steps: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Updating database: $steps steps for ${DateUtils.getCurrentDate()}")
            repository.insertOrUpdateSteps(DateUtils.getCurrentDate(), steps)
        }
    }

    fun getProgressPercentage(): Float {
        val today = todaySteps.value?.steps ?: 0
        return (today.toFloat() / dailyGoal.value.toFloat()).coerceIn(0f, 1f)
    }

    fun calculateCalories(steps: Int, weight: Int): Int {
        return ((steps * weight * 0.57) / 1000).toInt()
    }

    fun getTodayCalories(): Int {
        val steps = todaySteps.value?.steps ?: 0
        val weight = userWeight.value
        return calculateCalories(steps, weight)
    }

    fun getDebugInfo(): String {
        return improvedStepManager.getDebugInfo()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "=== VIEWMODEL CLEARED ===")
        improvedStepManager.stopTracking()
    }
}
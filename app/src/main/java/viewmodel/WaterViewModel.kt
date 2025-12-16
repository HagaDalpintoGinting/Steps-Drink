package com.example.stepdrink.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepdrink.data.local.database.AppDatabase
import com.example.stepdrink.data.local.PreferencesManager
import com.example.stepdrink.data.local.entity.WaterRecord
import com.example.stepdrink.data.repository.WaterRepository
import com.example.stepdrink.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository = WaterRepository(
        AppDatabase.getDatabase(application).waterDao()
    )

    private val preferencesManager = PreferencesManager(application)

    val dailyGoal: StateFlow<Int> = preferencesManager.waterGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    val todayWaterRecords: StateFlow<List<WaterRecord>> =
        repository.getWaterRecordsByDate(DateUtils.getCurrentDate())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotalWater: StateFlow<Int> =
        repository.getTotalWaterByDate(DateUtils.getCurrentDate())
            .map { it ?: 0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val last7DaysWater: StateFlow<List<WaterRecord>> =
        repository.getLast7DaysWater()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWater(amount: Int) {
        viewModelScope.launch {
            repository.addWaterRecord(DateUtils.getCurrentDate(), amount)
        }
    }

    fun deleteWaterRecord(record: WaterRecord) {
        viewModelScope.launch {
            repository.deleteWaterRecord(record)
        }
    }
}
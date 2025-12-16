package com.example.stepdrink.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepdrink.data.local.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Pengguna")

    val stepGoal: StateFlow<Int> = preferencesManager.stepGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000)

    val waterGoal: StateFlow<Int> = preferencesManager.waterGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    fun updateUserName(name: String) {
        viewModelScope.launch {
            preferencesManager.saveUserName(name)
        }
    }

    fun updateStepGoal(goal: Int) {
        viewModelScope.launch {
            preferencesManager.saveStepGoal(goal)
        }
    }

    fun updateWaterGoal(goal: Int) {
        viewModelScope.launch {
            preferencesManager.saveWaterGoal(goal)
        }
    }
}
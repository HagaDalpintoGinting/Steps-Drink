package com.example.stepdrink.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val STEP_GOAL = intPreferencesKey("step_goal")
        val WATER_GOAL = intPreferencesKey("water_goal")
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Pengguna"
    }

    val stepGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[STEP_GOAL] ?: 10000
    }

    val waterGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[WATER_GOAL] ?: 2000
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun saveStepGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[STEP_GOAL] = goal
        }
    }

    suspend fun saveWaterGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[WATER_GOAL] = goal
        }
    }
}
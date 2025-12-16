package com.example.stepdrink.data.repository

import com.example.stepdrink.data.local.dao.StepDao
import com.example.stepdrink.data.local.entity.StepRecord
import kotlinx.coroutines.flow.Flow

class StepRepository(private val stepDao: StepDao) {

    fun getStepsByDate(date: String): Flow<StepRecord?> {
        return stepDao.getStepsByDate(date)
    }

    fun getLast7DaysSteps(): Flow<List<StepRecord>> {
        return stepDao.getLast7DaysSteps()
    }

    suspend fun insertOrUpdateSteps(date: String, steps: Int) {
        stepDao.insertStepRecord(StepRecord(date = date, steps = steps))
    }

    suspend fun updateStepRecord(stepRecord: StepRecord) {
        stepDao.updateStepRecord(stepRecord)
    }
}
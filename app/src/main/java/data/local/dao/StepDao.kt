package com.example.stepdrink.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.stepdrink.data.local.entity.StepRecord

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepRecord(stepRecord: StepRecord)

    @Query("SELECT * FROM step_records WHERE date = :date")
    fun getStepsByDate(date: String): Flow<StepRecord?>

    @Query("SELECT * FROM step_records ORDER BY date DESC LIMIT 7")
    fun getLast7DaysSteps(): Flow<List<StepRecord>>

    @Query("SELECT * FROM step_records ORDER BY date DESC")
    fun getAllSteps(): Flow<List<StepRecord>>

    @Update
    suspend fun updateStepRecord(stepRecord: StepRecord)
}
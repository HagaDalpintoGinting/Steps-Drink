package com.example.stepdrink.data.local.dao

import androidx.room.*
import com.example.stepdrink.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Insert
    suspend fun insertWaterRecord(waterRecord: WaterRecord)

    @Query("SELECT * FROM water_records WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterRecordsByDate(date: String): Flow<List<WaterRecord>>

    @Query("SELECT SUM(amount) FROM water_records WHERE date = :date")
    fun getTotalWaterByDate(date: String): Flow<Int?>

    @Query("SELECT * FROM water_records ORDER BY date DESC LIMIT 7")
    fun getLast7DaysWater(): Flow<List<WaterRecord>>

    @Delete
    suspend fun deleteWaterRecord(waterRecord: WaterRecord)
}
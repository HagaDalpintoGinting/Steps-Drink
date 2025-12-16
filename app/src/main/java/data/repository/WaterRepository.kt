package com.example.stepdrink.data.repository

import com.example.stepdrink.data.local.dao.WaterDao
import com.example.stepdrink.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterDao: WaterDao) {

    fun getWaterRecordsByDate(date: String): Flow<List<WaterRecord>> {
        return waterDao.getWaterRecordsByDate(date)
    }

    fun getTotalWaterByDate(date: String): Flow<Int?> {
        return waterDao.getTotalWaterByDate(date)
    }

    fun getLast7DaysWater(): Flow<List<WaterRecord>> {
        return waterDao.getLast7DaysWater()
    }

    suspend fun addWaterRecord(date: String, amount: Int) {
        waterDao.insertWaterRecord(WaterRecord(date = date, amount = amount))
    }

    suspend fun deleteWaterRecord(waterRecord: WaterRecord) {
        waterDao.deleteWaterRecord(waterRecord)
    }
}
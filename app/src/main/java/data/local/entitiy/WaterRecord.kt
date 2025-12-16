package com.example.stepdrink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_records")
data class WaterRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val amount: Int, // dalam ml
    val timestamp: Long = System.currentTimeMillis()
)


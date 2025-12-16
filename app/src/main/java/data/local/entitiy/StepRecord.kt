package com.example.stepdrink.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val steps: Int,
    val timestamp: Long = System.currentTimeMillis()
)


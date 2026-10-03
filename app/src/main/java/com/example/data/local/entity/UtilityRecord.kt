package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utility_records")
data class UtilityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "electricity" or "water"
    val amount: Double,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val readingValue: Double = 0.0,
    val notes: String = ""
)

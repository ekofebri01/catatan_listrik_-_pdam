package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electricity_records")
data class ElectricityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val nominal: Double,
    val kwhReceived: Double,
    val tokenNumber: String = "",
    val meterNumber: String = "",
    val customerName: String = "",
    val tariffType: String = "R-1/1300 VA",
    val adminFee: Double = 2500.0,
    val ppjAmount: Double = 0.0,
    val isAppliedToMeter: Boolean = true,
    val notes: String = "",
    val merchant: String = "PLN Mobile"
)

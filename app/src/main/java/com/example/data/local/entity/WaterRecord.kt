package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_records")
data class WaterRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodMonth: Int, // 1 to 12
    val periodYear: Int, // e.g. 2026
    val readingDateEpochMillis: Long = System.currentTimeMillis(),
    val previousMeterReading: Double,
    val currentMeterReading: Double,
    val usageM3: Double,
    val baseFee: Double = 15000.0,
    val ratePerM3: Double = 3200.0,
    val adminFee: Double = 2500.0,
    val maintenanceFee: Double = 3000.0,
    val estimatedBillAmount: Double,
    val actualPaidAmount: Double = 0.0,
    val isPaid: Boolean = false,
    val paidDateEpochMillis: Long? = null,
    val meterNumber: String = "",
    val pdamRegionName: String = "PDAM Tirta",
    val customerName: String = "",
    val notes: String = ""
)

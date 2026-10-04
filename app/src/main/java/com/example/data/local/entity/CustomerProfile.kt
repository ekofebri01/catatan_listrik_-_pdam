package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_profiles")
data class CustomerProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val plnMeterNumber: String = "",
    val plnTariffType: String = "R-1/1300 VA",
    val customRatePerKwh: Double = 0.0,
    val pdamMeterNumber: String = "",
    val pdamName: String = "PDAM",
    val isDefault: Boolean = false
)

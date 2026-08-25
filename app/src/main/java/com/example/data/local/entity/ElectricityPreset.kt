package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electricity_presets")
data class ElectricityPreset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nominal: Double,
    val kwhReceived: Double,
    val adminFee: Double = 2500.0,
    val ppjTaxPercent: Double = 3.0,
    val label: String = "",
    val orderIndex: Int = 0
)

package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utility_config")
data class UtilityConfig(
    @PrimaryKey val id: Int = 1,
    val defaultCustomerName: String = "Rumah Utama",
    val defaultPlnMeterNumber: String = "542109876543",
    val defaultPlnTariff: String = "R-1/1300 VA",
    val plnRatePerKwh: Double = 1444.70,
    val defaultPdamMeterNumber: String = "081298412",
    val defaultPdamName: String = "PDAM Surya Sembada",
    val defaultWaterBaseFee: Double = 15000.0,
    val defaultWaterRatePerM3: Double = 3200.0,
    val defaultWaterAdminFee: Double = 2500.0,
    val defaultWaterMaintenanceFee: Double = 3000.0,
    val waterTariffTier1LimitM3: Double = 10.0,
    val waterTariffTier1Rate: Double = 2400.0,
    val waterTariffTier2LimitM3: Double = 20.0,
    val waterTariffTier2Rate: Double = 3600.0,
    val waterTariffTier3Rate: Double = 5200.0,
    val isTierPricingEnabled: Boolean = true
)

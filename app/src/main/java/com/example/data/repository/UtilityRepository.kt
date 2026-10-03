package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.data.local.entity.ElectricityRecord
import com.example.data.local.entity.UtilityConfig
import com.example.data.local.entity.UtilityRecord
import com.example.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

class UtilityRepository(private val database: AppDatabase) {
    private val electricityDao = database.electricityDao()
    private val waterDao = database.waterDao()
    private val presetDao = database.presetDao()
    private val configDao = database.utilityConfigDao()
    private val profileDao = database.customerProfileDao()
    private val utilityRecordDao = database.utilityRecordDao()

    // Utility Records
    val allUtilityRecords: Flow<List<UtilityRecord>> = utilityRecordDao.getAllRecords()
    suspend fun insertUtilityRecord(record: UtilityRecord): Long = utilityRecordDao.insertRecord(record)

    // Electricity Records
    val allElectricityRecords: Flow<List<ElectricityRecord>> = electricityDao.getAllRecords()
    suspend fun insertElectricityRecord(record: ElectricityRecord): Long = electricityDao.insertRecord(record)
    suspend fun updateElectricityRecord(record: ElectricityRecord) = electricityDao.updateRecord(record)
    suspend fun deleteElectricityRecord(record: ElectricityRecord) = electricityDao.deleteRecord(record)
    suspend fun deleteElectricityById(id: Long) = electricityDao.deleteById(id)

    // Water Records
    val allWaterRecords: Flow<List<WaterRecord>> = waterDao.getAllRecords()
    suspend fun getLatestWaterRecord(): WaterRecord? = waterDao.getLatestRecord()
    suspend fun insertWaterRecord(record: WaterRecord): Long = waterDao.insertRecord(record)
    suspend fun updateWaterRecord(record: WaterRecord) = waterDao.updateRecord(record)
    suspend fun deleteWaterRecord(record: WaterRecord) = waterDao.deleteRecord(record)
    suspend fun deleteWaterById(id: Long) = waterDao.deleteById(id)

    // Presets
    val allPresets: Flow<List<ElectricityPreset>> = presetDao.getAllPresets()
    suspend fun insertPreset(preset: ElectricityPreset): Long = presetDao.insertPreset(preset)
    suspend fun updatePreset(preset: ElectricityPreset) = presetDao.updatePreset(preset)
    suspend fun deletePreset(preset: ElectricityPreset) = presetDao.deletePreset(preset)
    suspend fun deletePresetById(id: Long) = presetDao.deleteById(id)

    // Config
    val config: Flow<UtilityConfig?> = configDao.getConfig()
    suspend fun getConfigOnce(): UtilityConfig? = configDao.getConfigOnce()
    suspend fun saveConfig(config: UtilityConfig) = configDao.saveConfig(config)

    // Profiles
    val allProfiles: Flow<List<CustomerProfile>> = profileDao.getAllProfiles()
    suspend fun insertProfile(profile: CustomerProfile): Long = profileDao.insertProfile(profile)
    suspend fun updateProfile(profile: CustomerProfile) = profileDao.updateProfile(profile)
    suspend fun deleteProfile(profile: CustomerProfile) = profileDao.deleteProfile(profile)

    // Water calculation helper based on tiers or flat rate
    fun calculateWaterBill(usageM3: Double, config: UtilityConfig): Double {
        val effectiveUsage = if (usageM3 < 0) 0.0 else usageM3
        val waterCost = if (config.isTierPricingEnabled) {
            var cost = 0.0
            var remaining = effectiveUsage

            // Tier 1 (e.g. 0 - 10 m³)
            val t1Limit = config.waterTariffTier1LimitM3
            val t1Usage = minOf(remaining, t1Limit)
            cost += t1Usage * config.waterTariffTier1Rate
            remaining -= t1Usage

            if (remaining > 0) {
                // Tier 2 (e.g. 11 - 20 m³)
                val t2Limit = config.waterTariffTier2LimitM3 - t1Limit
                val t2Usage = minOf(remaining, t2Limit)
                cost += t2Usage * config.waterTariffTier2Rate
                remaining -= t2Usage
            }

            if (remaining > 0) {
                // Tier 3 (> 20 m³)
                cost += remaining * config.waterTariffTier3Rate
            }
            cost
        } else {
            effectiveUsage * config.defaultWaterRatePerM3
        }

        return config.defaultWaterBaseFee + waterCost + config.defaultWaterAdminFee + config.defaultWaterMaintenanceFee
    }

    // Electricity kWh estimation from nominal using tariff or nearest preset
    fun estimateKwhForNominal(nominal: Double, presets: List<ElectricityPreset>, config: UtilityConfig): Double {
        val matchingPreset = presets.firstOrNull { it.nominal == nominal }
        if (matchingPreset != null) {
            return matchingPreset.kwhReceived
        }
        // Formula estimation: (Nominal - Admin - PPJ) / Tarif per kWh
        val admin = 2500.0
        val ppjPercent = 0.03
        val netNominal = (nominal - admin) * (1.0 - ppjPercent)
        val rate = if (config.plnRatePerKwh > 0) config.plnRatePerKwh else 1444.70
        val estimated = netNominal / rate
        return if (estimated > 0) Math.round(estimated * 100.0) / 100.0 else 0.0
    }
}

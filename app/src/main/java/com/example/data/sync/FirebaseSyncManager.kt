package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.data.local.entity.ElectricityRecord
import com.example.data.local.entity.UtilityConfig
import com.example.data.local.entity.WaterRecord
import com.example.data.repository.UtilityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager(
    private val context: Context,
    private val repository: UtilityRepository
) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (e: Exception) { null }

    suspend fun syncLocalToCloud(
        electricityRecords: List<ElectricityRecord>,
        waterRecords: List<WaterRecord>,
        presets: List<ElectricityPreset>,
        config: UtilityConfig?,
        profiles: List<CustomerProfile>
    ): Result<String> {
        val user = currentUser ?: return Result.failure(Exception("Silakan login Google terlebih dahulu untuk sinkronisasi"))
        return try {
            val userDoc = firestore.collection("users").document(user.uid)
            val batch = firestore.batch()

            // Save config
            if (config != null) {
                val configData = hashMapOf(
                    "plnRatePerKwh" to config.plnRatePerKwh,
                    "defaultCustomerName" to config.defaultCustomerName,
                    "defaultPlnMeterNumber" to config.defaultPlnMeterNumber,
                    "defaultPlnTariff" to config.defaultPlnTariff,
                    "defaultPdamMeterNumber" to config.defaultPdamMeterNumber,
                    "defaultPdamName" to config.defaultPdamName,
                    "defaultWaterBaseFee" to config.defaultWaterBaseFee,
                    "defaultWaterRatePerM3" to config.defaultWaterRatePerM3,
                    "defaultWaterAdminFee" to config.defaultWaterAdminFee,
                    "defaultWaterMaintenanceFee" to config.defaultWaterMaintenanceFee,
                    "waterTariffTier1LimitM3" to config.waterTariffTier1LimitM3,
                    "waterTariffTier1Rate" to config.waterTariffTier1Rate,
                    "waterTariffTier2LimitM3" to config.waterTariffTier2LimitM3,
                    "waterTariffTier2Rate" to config.waterTariffTier2Rate,
                    "waterTariffTier3Rate" to config.waterTariffTier3Rate,
                    "isTierPricingEnabled" to config.isTierPricingEnabled,
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.set(userDoc.collection("settings").document("config"), configData, SetOptions.merge())
            }

            // Sync electricity records
            for (rec in electricityRecords) {
                val data = hashMapOf(
                    "nominal" to rec.nominal,
                    "kwhReceived" to rec.kwhReceived,
                    "tokenNumber" to rec.tokenNumber,
                    "meterNumber" to rec.meterNumber,
                    "customerName" to rec.customerName,
                    "tariffType" to rec.tariffType,
                    "dateEpochMillis" to rec.dateEpochMillis,
                    "notes" to rec.notes,
                    "merchant" to rec.merchant
                )
                batch.set(userDoc.collection("electricity_records").document(rec.id.toString()), data, SetOptions.merge())
            }

            // Sync water records
            for (w in waterRecords) {
                val data = hashMapOf(
                    "periodMonth" to w.periodMonth,
                    "periodYear" to w.periodYear,
                    "readingDateEpochMillis" to w.readingDateEpochMillis,
                    "previousMeterReading" to w.previousMeterReading,
                    "currentMeterReading" to w.currentMeterReading,
                    "usageM3" to w.usageM3,
                    "estimatedBillAmount" to w.estimatedBillAmount,
                    "actualPaidAmount" to w.actualPaidAmount,
                    "isPaid" to w.isPaid,
                    "meterNumber" to w.meterNumber,
                    "pdamRegionName" to w.pdamRegionName,
                    "customerName" to w.customerName,
                    "notes" to w.notes
                )
                batch.set(userDoc.collection("water_records").document(w.id.toString()), data, SetOptions.merge())
            }

            // Sync presets
            for (preset in presets) {
                val data = hashMapOf(
                    "nominal" to preset.nominal,
                    "kwhReceived" to preset.kwhReceived,
                    "adminFee" to preset.adminFee,
                    "ppjTaxPercent" to preset.ppjTaxPercent,
                    "label" to preset.label,
                    "orderIndex" to preset.orderIndex
                )
                batch.set(userDoc.collection("presets").document(preset.id.toString()), data, SetOptions.merge())
            }

            batch.commit().await()
            Result.success("Data berhasil disinkronkan ke Cloud Firestore!")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Sync failed", e)
            Result.failure(e)
        }
    }
}

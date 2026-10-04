package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.CustomerProfileDao
import com.example.data.local.dao.ElectricityDao
import com.example.data.local.dao.PresetDao
import com.example.data.local.dao.UtilityConfigDao
import com.example.data.local.dao.UtilityRecordDao
import com.example.data.local.dao.WaterDao
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.data.local.entity.ElectricityRecord
import com.example.data.local.entity.UtilityConfig
import com.example.data.local.entity.UtilityRecord
import com.example.data.local.entity.WaterRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ElectricityRecord::class,
        WaterRecord::class,
        ElectricityPreset::class,
        UtilityConfig::class,
        CustomerProfile::class,
        UtilityRecord::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun electricityDao(): ElectricityDao
    abstract fun waterDao(): WaterDao
    abstract fun presetDao(): PresetDao
    abstract fun utilityConfigDao(): UtilityConfigDao
    abstract fun customerProfileDao(): CustomerProfileDao
    abstract fun utilityRecordDao(): UtilityRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "volthydro_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Deduplicate and ensure clean presets (e.g. 20k, 50k, 100k, 200k, 500k, 1jt)
            val existingPresets = database.presetDao().getAllPresetsList()
            if (existingPresets.isEmpty()) {
                val defaultPresets = listOf(
                    ElectricityPreset(nominal = 20000.0, kwhReceived = 13.2, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Minimal", orderIndex = 1),
                    ElectricityPreset(nominal = 50000.0, kwhReceived = 33.2, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Populer", orderIndex = 2),
                    ElectricityPreset(nominal = 100000.0, kwhReceived = 66.8, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Rekomendasi", orderIndex = 3),
                    ElectricityPreset(nominal = 200000.0, kwhReceived = 133.6, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Bulanan", orderIndex = 4),
                    ElectricityPreset(nominal = 500000.0, kwhReceived = 334.0, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Kapasitas Besar", orderIndex = 5),
                    ElectricityPreset(nominal = 1000000.0, kwhReceived = 668.0, adminFee = 2500.0, ppjTaxPercent = 3.0, label = "Maksimal", orderIndex = 6)
                )
                database.presetDao().insertPresets(defaultPresets)
            } else {
                // If duplicates exist in DB, clean them up and preserve only unique nominals
                val deduplicated = existingPresets.distinctBy { it.nominal }
                if (deduplicated.size < existingPresets.size) {
                    database.presetDao().deleteAll()
                    database.presetDao().insertPresets(deduplicated)
                }
            }

            // Default Config
            if (database.utilityConfigDao().getConfigOnce() == null) {
                database.utilityConfigDao().saveConfig(UtilityConfig())
            }

            // Default Profiles (Seed locations with different PLN Tariffs)
            if (database.customerProfileDao().getCount() == 0) {
                database.customerProfileDao().insertProfile(
                    CustomerProfile(
                        name = "Rumah Utama",
                        plnMeterNumber = "542109876543",
                        plnTariffType = "R-1/2200 VA",
                        pdamMeterNumber = "081298412",
                        pdamName = "PDAM Surya Sembada",
                        isDefault = true
                    )
                )
                database.customerProfileDao().insertProfile(
                    CustomerProfile(
                        name = "Kios / Usaha",
                        plnMeterNumber = "542109876599",
                        plnTariffType = "R-1M/900 VA",
                        pdamMeterNumber = "081298499",
                        pdamName = "PDAM Tirta Kencana",
                        isDefault = false
                    )
                )
            }
        }
    }
}

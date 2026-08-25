package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ElectricityPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM electricity_presets ORDER BY nominal ASC")
    fun getAllPresets(): Flow<List<ElectricityPreset>>

    @Query("SELECT * FROM electricity_presets ORDER BY nominal ASC")
    suspend fun getAllPresetsList(): List<ElectricityPreset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: ElectricityPreset): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<ElectricityPreset>)

    @Update
    suspend fun updatePreset(preset: ElectricityPreset)

    @Delete
    suspend fun deletePreset(preset: ElectricityPreset)

    @Query("DELETE FROM electricity_presets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM electricity_presets")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM electricity_presets")
    suspend fun getCount(): Int
}

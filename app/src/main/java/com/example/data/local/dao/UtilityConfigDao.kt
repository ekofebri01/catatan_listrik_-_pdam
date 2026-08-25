package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UtilityConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface UtilityConfigDao {
    @Query("SELECT * FROM utility_config WHERE id = 1")
    fun getConfig(): Flow<UtilityConfig?>

    @Query("SELECT * FROM utility_config WHERE id = 1")
    suspend fun getConfigOnce(): UtilityConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: UtilityConfig)

    @Update
    suspend fun updateConfig(config: UtilityConfig)
}

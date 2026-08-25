package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ElectricityRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricityDao {
    @Query("SELECT * FROM electricity_records ORDER BY dateEpochMillis DESC")
    fun getAllRecords(): Flow<List<ElectricityRecord>>

    @Query("SELECT * FROM electricity_records WHERE id = :id")
    suspend fun getRecordById(id: Long): ElectricityRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ElectricityRecord): Long

    @Update
    suspend fun updateRecord(record: ElectricityRecord)

    @Delete
    suspend fun deleteRecord(record: ElectricityRecord)

    @Query("DELETE FROM electricity_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(nominal) FROM electricity_records WHERE dateEpochMillis >= :startMillis AND dateEpochMillis <= :endMillis")
    fun getTotalSpentInPeriod(startMillis: Long, endMillis: Long): Flow<Double?>

    @Query("SELECT SUM(kwhReceived) FROM electricity_records WHERE dateEpochMillis >= :startMillis AND dateEpochMillis <= :endMillis")
    fun getTotalKwhInPeriod(startMillis: Long, endMillis: Long): Flow<Double?>
}

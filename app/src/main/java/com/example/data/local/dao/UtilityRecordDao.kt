package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UtilityRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UtilityRecordDao {
    @Query("SELECT * FROM utility_records ORDER BY dateEpochMillis DESC")
    fun getAllRecords(): Flow<List<UtilityRecord>>

    @Query("SELECT * FROM utility_records WHERE type = :type ORDER BY dateEpochMillis DESC")
    fun getRecordsByType(type: String): Flow<List<UtilityRecord>>

    @Query("SELECT * FROM utility_records WHERE id = :id")
    suspend fun getRecordById(id: Long): UtilityRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: UtilityRecord): Long

    @Update
    suspend fun updateRecord(record: UtilityRecord)

    @Delete
    suspend fun deleteRecord(record: UtilityRecord)

    @Query("DELETE FROM utility_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(amount) FROM utility_records WHERE type = :type AND dateEpochMillis >= :startMillis AND dateEpochMillis <= :endMillis")
    fun getTotalAmountByTypeInPeriod(type: String, startMillis: Long, endMillis: Long): Flow<Double?>
}

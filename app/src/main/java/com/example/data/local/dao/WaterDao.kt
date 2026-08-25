package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_records ORDER BY periodYear DESC, periodMonth DESC, readingDateEpochMillis DESC")
    fun getAllRecords(): Flow<List<WaterRecord>>

    @Query("SELECT * FROM water_records WHERE id = :id")
    suspend fun getRecordById(id: Long): WaterRecord?

    @Query("SELECT * FROM water_records ORDER BY readingDateEpochMillis DESC LIMIT 1")
    suspend fun getLatestRecord(): WaterRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WaterRecord): Long

    @Update
    suspend fun updateRecord(record: WaterRecord)

    @Delete
    suspend fun deleteRecord(record: WaterRecord)

    @Query("DELETE FROM water_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(usageM3) FROM water_records WHERE periodYear = :year")
    fun getTotalUsageInYear(year: Int): Flow<Double?>

    @Query("SELECT SUM(estimatedBillAmount) FROM water_records WHERE isPaid = 0")
    fun getUnpaidBillTotal(): Flow<Double?>
}

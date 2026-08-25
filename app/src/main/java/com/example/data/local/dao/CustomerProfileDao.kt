package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CustomerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerProfileDao {
    @Query("SELECT * FROM customer_profiles ORDER BY isDefault DESC, name ASC")
    fun getAllProfiles(): Flow<List<CustomerProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CustomerProfile): Long

    @Update
    suspend fun updateProfile(profile: CustomerProfile)

    @Delete
    suspend fun deleteProfile(profile: CustomerProfile)

    @Query("DELETE FROM customer_profiles WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM customer_profiles")
    suspend fun getCount(): Int
}

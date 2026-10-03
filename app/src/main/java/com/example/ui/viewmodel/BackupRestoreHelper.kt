package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.data.repository.UtilityRepository
import com.example.data.local.entity.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupRestoreHelper(private val repository: UtilityRepository) {

    suspend fun exportData(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val elRecords = repository.allElectricityRecords.first()
                val wtRecords = repository.allWaterRecords.first()
                val utilityRecords = repository.allUtilityRecords.first()
                val presets = repository.allPresets.first()
                val config = repository.config.first()
                val profiles = repository.allProfiles.first()

                val backupObj = JsonObject()
                val gson = Gson()
                
                backupObj.add("electricityRecords", gson.toJsonTree(elRecords))
                backupObj.add("waterRecords", gson.toJsonTree(wtRecords))
                backupObj.add("utilityRecords", gson.toJsonTree(utilityRecords))
                backupObj.add("presets", gson.toJsonTree(presets))
                backupObj.add("config", gson.toJsonTree(config))
                backupObj.add("profiles", gson.toJsonTree(profiles))
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(backupObj.toString().toByteArray())
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Backup berhasil disimpan!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal menyimpan backup", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun importData(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                var jsonString = ""
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    jsonString = inputStream.bufferedReader().use { it.readText() }
                }
                if (jsonString.isBlank()) return@withContext

                val gson = Gson()
                val backupObj = gson.fromJson(jsonString, JsonObject::class.java)

                if (backupObj.has("electricityRecords")) {
                    val elRecords = gson.fromJson(backupObj.getAsJsonArray("electricityRecords"), Array<ElectricityRecord>::class.java).toList()
                    elRecords.forEach { repository.insertElectricityRecord(it) }
                }
                
                if (backupObj.has("waterRecords")) {
                    val wtRecords = gson.fromJson(backupObj.getAsJsonArray("waterRecords"), Array<WaterRecord>::class.java).toList()
                    wtRecords.forEach { repository.insertWaterRecord(it) }
                }

                if (backupObj.has("utilityRecords")) {
                    val utilityRecords = gson.fromJson(backupObj.getAsJsonArray("utilityRecords"), Array<UtilityRecord>::class.java).toList()
                    utilityRecords.forEach { repository.insertUtilityRecord(it) }
                }
                
                if (backupObj.has("presets")) {
                    val presets = gson.fromJson(backupObj.getAsJsonArray("presets"), Array<ElectricityPreset>::class.java).toList()
                    presets.forEach { repository.insertPreset(it) }
                }
                
                if (backupObj.has("config")) {
                    val config = gson.fromJson(backupObj.getAsJsonObject("config"), UtilityConfig::class.java)
                    if (config != null) repository.saveConfig(config)
                }
                
                if (backupObj.has("profiles")) {
                    val profiles = gson.fromJson(backupObj.getAsJsonArray("profiles"), Array<CustomerProfile>::class.java).toList()
                    profiles.forEach { repository.insertProfile(it) }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Data berhasil dipulihkan!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal memulihkan data. Format tidak sesuai.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

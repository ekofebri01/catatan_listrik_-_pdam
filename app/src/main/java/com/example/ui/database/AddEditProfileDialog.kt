package com.example.ui.database

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.CustomerProfile

@Composable
fun AddEditProfileDialog(
    profile: CustomerProfile? = null,
    onDismiss: () -> Unit,
    onSave: (CustomerProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var plnMeterNumber by remember { mutableStateOf(profile?.plnMeterNumber ?: "") }
    var plnTariffType by remember { mutableStateOf(profile?.plnTariffType ?: "R-1/1300 VA") }
    var pdamMeterNumber by remember { mutableStateOf(profile?.pdamMeterNumber ?: "") }
    var pdamName by remember { mutableStateOf(profile?.pdamName ?: "PDAM") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (profile == null) "Tambah Profil Meteran" else "Edit Profil Meteran",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_profile_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lokasi / Pelanggan") },
                    placeholder = { Text("Rumah Utama, Kos, Toko...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = plnMeterNumber,
                    onValueChange = { plnMeterNumber = it },
                    label = { Text("No. Meter / ID PLN") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pln_meter_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = plnTariffType,
                    onValueChange = { plnTariffType = it },
                    label = { Text("Daya Listrik") },
                    placeholder = { Text("R-1/900 VA, R-1/1300 VA, dll") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pln_tariff_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pdamMeterNumber,
                    onValueChange = { pdamMeterNumber = it },
                    label = { Text("No. Sambungan PDAM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pdam_meter_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pdamName,
                    onValueChange = { pdamName = it },
                    label = { Text("Instansi PDAM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_pdam_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_profile_button")
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val updated = profile?.copy(
                                name = name.ifBlank { "Lokasi" },
                                plnMeterNumber = plnMeterNumber,
                                plnTariffType = plnTariffType,
                                pdamMeterNumber = pdamMeterNumber,
                                pdamName = pdamName
                            ) ?: CustomerProfile(
                                name = name.ifBlank { "Lokasi" },
                                plnMeterNumber = plnMeterNumber,
                                plnTariffType = plnTariffType,
                                pdamMeterNumber = pdamMeterNumber,
                                pdamName = pdamName
                            )
                            onSave(updated)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_profile_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Profil", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

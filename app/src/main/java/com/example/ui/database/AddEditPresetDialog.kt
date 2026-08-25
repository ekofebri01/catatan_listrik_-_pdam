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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.ElectricityPreset
import com.example.ui.theme.ElectricGoldPrimary

@Composable
fun AddEditPresetDialog(
    preset: ElectricityPreset? = null,
    onDismiss: () -> Unit,
    onSave: (ElectricityPreset) -> Unit
) {
    var nominalText by remember {
        mutableStateOf(preset?.nominal?.toLong()?.toString() ?: "50000")
    }
    var kwhText by remember {
        mutableStateOf(preset?.kwhReceived?.toString() ?: "33.2")
    }
    var labelText by remember {
        mutableStateOf(preset?.label ?: "Standar")
    }
    var adminFeeText by remember {
        mutableStateOf(preset?.adminFee?.toLong()?.toString() ?: "2500")
    }
    var ppjPercentText by remember {
        mutableStateOf(preset?.ppjTaxPercent?.toString() ?: "3.0")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, ElectricGoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = ElectricGoldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (preset == null) "Tambah Preset Listrik" else "Edit Preset Pembelian",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_preset_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Konfigurasi berapa kWh yang didapat saat membeli nominal ini (misal 50k dpt 33.2 kWh, 100k dpt 66.8 kWh).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = nominalText,
                    onValueChange = { nominalText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal Pembelian (Rp)") },
                    placeholder = { Text("Contoh: 50000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("preset_nominal_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricGoldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = kwhText,
                    onValueChange = { kwhText = it },
                    label = { Text("Jumlah kWh yang Didapat") },
                    placeholder = { Text("Contoh: 33.2") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("preset_kwh_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricGoldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = labelText,
                    onValueChange = { labelText = it },
                    label = { Text("Label / Catatan Preset") },
                    placeholder = { Text("Populer, Hemat, R1-1300, dll") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("preset_label_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_preset_button")
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val nominalVal = nominalText.toDoubleOrNull() ?: 50000.0
                            val kwhVal = kwhText.toDoubleOrNull() ?: 33.2
                            val adminVal = adminFeeText.toDoubleOrNull() ?: 2500.0
                            val ppjVal = ppjPercentText.toDoubleOrNull() ?: 3.0

                            val updated = preset?.copy(
                                nominal = nominalVal,
                                kwhReceived = kwhVal,
                                label = labelText,
                                adminFee = adminVal,
                                ppjTaxPercent = ppjVal
                            ) ?: ElectricityPreset(
                                nominal = nominalVal,
                                kwhReceived = kwhVal,
                                label = labelText,
                                adminFee = adminVal,
                                ppjTaxPercent = ppjVal
                            )
                            onSave(updated)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_preset_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricGoldPrimary,
                            contentColor = Color(0xFF1E1B00)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan ke Database", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

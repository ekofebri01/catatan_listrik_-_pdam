package com.example.ui.database

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.PlnTariffHelper
import com.example.data.local.entity.ElectricityPreset
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.TextMutedDark

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
    var selectedTariffCategory by remember {
        mutableStateOf(preset?.tariffCategory ?: "SEMUA")
    }

    var showTariffDropdown by remember { mutableStateOf(false) }

    val categoryOption = if (selectedTariffCategory == "SEMUA") null else PlnTariffHelper.getOptionByCode(selectedTariffCategory)

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

                Spacer(modifier = Modifier.height(14.dp))

                // Tariff Category Selector
                Text(
                    text = "Golongan Daya Listrik Preset",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ElectricGoldBorder, RoundedCornerShape(12.dp))
                            .clickable { showTariffDropdown = true }
                            .testTag("preset_tariff_category_dropdown"),
                        color = ElectricGoldSubtle
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (selectedTariffCategory == "SEMUA") "Semua Daya (Umum)" else (categoryOption?.name ?: selectedTariffCategory),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (categoryOption != null) "Tarif: Rp ${categoryOption.ratePerKwh}/kWh" else "Berlaku umum",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMutedDark
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ElectricGoldPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = showTariffDropdown,
                        onDismissRequest = { showTariffDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Semua Daya (Umum)", fontWeight = FontWeight.Bold) },
                            onClick = {
                                selectedTariffCategory = "SEMUA"
                                showTariffDropdown = false
                            }
                        )
                        PlnTariffHelper.TARIFF_OPTIONS.filter { it.code != "CUSTOM" }.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(option.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Rp ${option.ratePerKwh}/kWh • ${option.description}", fontSize = 11.sp, color = TextMutedDark)
                                    }
                                },
                                onClick = {
                                    selectedTariffCategory = option.code
                                    showTariffDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = kwhText,
                        onValueChange = { kwhText = it },
                        label = { Text("Jumlah kWh Received") },
                        placeholder = { Text("Contoh: 33.2") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_kwh_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricGoldPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            val nom = nominalText.toDoubleOrNull() ?: 0.0
                            val admin = adminFeeText.toDoubleOrNull() ?: 2500.0
                            val ppj = ppjPercentText.toDoubleOrNull() ?: 3.0
                            val rate = categoryOption?.ratePerKwh ?: 1444.70
                            val calcKwh = PlnTariffHelper.calculateKwh(nom, rate, admin, ppj)
                            if (calcKwh > 0) {
                                kwhText = calcKwh.toString()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag("preset_auto_calculate_button")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hitung", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = labelText,
                    onValueChange = { labelText = it },
                    label = { Text("Label Preset") },
                    placeholder = { Text("Populer, Hemat, R1-1300, 2200VA, dll") },
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
                            val nominal = nominalText.toDoubleOrNull() ?: 0.0
                            val kwh = kwhText.toDoubleOrNull() ?: 0.0
                            val admin = adminFeeText.toDoubleOrNull() ?: 2500.0
                            val ppj = ppjPercentText.toDoubleOrNull() ?: 3.0

                            if (nominal > 0 && kwh > 0) {
                                val updated = preset?.copy(
                                    nominal = nominal,
                                    kwhReceived = kwh,
                                    adminFee = admin,
                                    ppjTaxPercent = ppj,
                                    label = labelText,
                                    tariffCategory = selectedTariffCategory
                                ) ?: ElectricityPreset(
                                    nominal = nominal,
                                    kwhReceived = kwh,
                                    adminFee = admin,
                                    ppjTaxPercent = ppj,
                                    label = labelText,
                                    tariffCategory = selectedTariffCategory
                                )
                                onSave(updated)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_preset_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGoldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Preset", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

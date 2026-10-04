package com.example.ui.database

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.PlnTariffHelper
import com.example.data.local.PlnTariffOption
import com.example.data.local.entity.CustomerProfile
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.TextMutedDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditProfileDialog(
    profile: CustomerProfile? = null,
    onDismiss: () -> Unit,
    onSave: (CustomerProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var plnMeterNumber by remember { mutableStateOf(profile?.plnMeterNumber ?: "") }
    var selectedTariffCode by remember { mutableStateOf(profile?.plnTariffType ?: "R-1/1300 VA") }
    var customRateText by remember { mutableStateOf(profile?.customRatePerKwh?.let { if (it > 0) it.toString() else "" } ?: "") }
    var pdamMeterNumber by remember { mutableStateOf(profile?.pdamMeterNumber ?: "") }
    var pdamName by remember { mutableStateOf(profile?.pdamName ?: "PDAM") }
    
    var showTariffDropdown by remember { mutableStateOf(false) }

    val currentOption = PlnTariffHelper.getOptionByCode(selectedTariffCode)

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
                    label = { Text("Nama Lokasi / Bangunan") },
                    placeholder = { Text("Rumah Utama (2200 VA), Kios (900 VA), dll") },
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

                Spacer(modifier = Modifier.height(14.dp))

                // PLN Power & Tariff Selector Box
                Text(
                    text = "Golongan Daya & Tarif PLN",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, ElectricGoldBorder, RoundedCornerShape(14.dp))
                            .clickable { showTariffDropdown = true }
                            .testTag("profile_pln_tariff_dropdown"),
                        color = ElectricGoldSubtle
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = ElectricGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentOption.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (currentOption.code == "CUSTOM") "Tarif Manual" else "Tarif PLN: Rp ${currentOption.ratePerKwh} / kWh (${currentOption.description})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMutedDark
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = ElectricGoldPrimary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTariffDropdown,
                        onDismissRequest = { showTariffDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        PlnTariffHelper.TARIFF_OPTIONS.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = option.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = if (option.code == "CUSTOM") "Set tarif manual per kWh" else "Rp ${option.ratePerKwh} / kWh • ${option.description}",
                                            fontSize = 11.sp,
                                            color = TextMutedDark
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTariffCode = option.code
                                    showTariffDropdown = false
                                }
                            )
                        }
                    }
                }

                if (selectedTariffCode == "CUSTOM") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customRateText,
                        onValueChange = { customRateText = it },
                        label = { Text("Tarif Kustom (Rp / kWh)") },
                        placeholder = { Text("Contoh: 1444.70") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_custom_rate_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                            val customRate = customRateText.toDoubleOrNull() ?: 0.0
                            val updated = profile?.copy(
                                name = name.ifBlank { "Lokasi" },
                                plnMeterNumber = plnMeterNumber,
                                plnTariffType = selectedTariffCode,
                                customRatePerKwh = customRate,
                                pdamMeterNumber = pdamMeterNumber,
                                pdamName = pdamName
                            ) ?: CustomerProfile(
                                name = name.ifBlank { "Lokasi" },
                                plnMeterNumber = plnMeterNumber,
                                plnTariffType = selectedTariffCode,
                                customRatePerKwh = customRate,
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

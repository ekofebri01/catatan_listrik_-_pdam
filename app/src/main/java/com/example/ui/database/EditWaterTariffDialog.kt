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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.entity.UtilityConfig
import com.example.ui.theme.WaterCyanDark
import com.example.ui.theme.WaterCyanPrimary
import com.example.ui.theme.WaterCyanSecondary

@Composable
fun EditWaterTariffDialog(
    config: UtilityConfig,
    onDismiss: () -> Unit,
    onSave: (UtilityConfig) -> Unit
) {
    var baseFeeText by remember { mutableStateOf(config.defaultWaterBaseFee.toLong().toString()) }
    var ratePerM3Text by remember { mutableStateOf(config.defaultWaterRatePerM3.toLong().toString()) }
    var adminFeeText by remember { mutableStateOf(config.defaultWaterAdminFee.toLong().toString()) }
    var maintenanceFeeText by remember { mutableStateOf(config.defaultWaterMaintenanceFee.toLong().toString()) }

    var isTierEnabled by remember { mutableStateOf(config.isTierPricingEnabled) }
    var t1RateText by remember { mutableStateOf(config.waterTariffTier1Rate.toLong().toString()) }
    var t2RateText by remember { mutableStateOf(config.waterTariffTier2Rate.toLong().toString()) }
    var t3RateText by remember { mutableStateOf(config.waterTariffTier3Rate.toLong().toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, WaterCyanSecondary.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
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
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = WaterCyanSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Konfigurasi Tarif PDAM",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_water_tariff_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Atur parameter biaya beban dan tarif per m³ PDAM agar perhitungan perkiraan tagihan air sesuai dengan aturan PDAM daerah Anda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Biaya Beban & Admin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = baseFeeText,
                        onValueChange = { baseFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Biaya Beban/Abonemen (Rp)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_config_base_fee_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = adminFeeText,
                        onValueChange = { adminFeeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Biaya Admin (Rp)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_config_admin_fee_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = maintenanceFeeText,
                    onValueChange = { maintenanceFeeText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Biaya Pemeliharaan Meter / Sampah (Rp)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("water_config_maintenance_fee_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tier pricing switch
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Gunakan Tarif Bertingkat (Blok)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTierEnabled) "Tarif berbeda untuk 0-10 m³, 11-20 m³, >20 m³" else "Menggunakan satu tarif flat per m³",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isTierEnabled,
                            onCheckedChange = { isTierEnabled = it },
                            modifier = Modifier.testTag("water_tier_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WaterCyanPrimary,
                                checkedTrackColor = WaterCyanSecondary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isTierEnabled) {
                    OutlinedTextField(
                        value = t1RateText,
                        onValueChange = { t1RateText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Blok 1 (0 - 10 m³) @ Rp/m³") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("water_tier1_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = t2RateText,
                        onValueChange = { t2RateText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Blok 2 (11 - 20 m³) @ Rp/m³") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("water_tier2_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = t3RateText,
                        onValueChange = { t3RateText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Blok 3 (> 20 m³) @ Rp/m³") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("water_tier3_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = ratePerM3Text,
                        onValueChange = { ratePerM3Text = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Tarif Flat per m³ (Rp)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("water_flat_rate_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_water_config_button")
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val updatedConfig = config.copy(
                                defaultWaterBaseFee = baseFeeText.toDoubleOrNull() ?: 15000.0,
                                defaultWaterAdminFee = adminFeeText.toDoubleOrNull() ?: 2500.0,
                                defaultWaterMaintenanceFee = maintenanceFeeText.toDoubleOrNull() ?: 3000.0,
                                defaultWaterRatePerM3 = ratePerM3Text.toDoubleOrNull() ?: 3200.0,
                                isTierPricingEnabled = isTierEnabled,
                                waterTariffTier1Rate = t1RateText.toDoubleOrNull() ?: 2400.0,
                                waterTariffTier2Rate = t2RateText.toDoubleOrNull() ?: 3600.0,
                                waterTariffTier3Rate = t3RateText.toDoubleOrNull() ?: 5200.0
                            )
                            onSave(updatedConfig)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_water_config_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WaterCyanPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Konfigurasi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

package com.example.ui.electricity

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.data.local.entity.ElectricityRecord
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSecondary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.UtilityViewModel
import java.util.Calendar

@Composable
fun AddEditElectricityDialog(
    initialRecord: ElectricityRecord? = null,
    presets: List<ElectricityPreset>,
    profiles: List<CustomerProfile>,
    defaultMeter: String,
    defaultSelectedLocation: String? = null,
    onDismiss: () -> Unit,
    onSave: (ElectricityRecord) -> Unit,
    onEstimateKwh: (Double) -> Double
) {
    val context = LocalContext.current
    var selectedDateMillis by remember {
        mutableStateOf(initialRecord?.dateEpochMillis ?: System.currentTimeMillis())
    }

    val initialProfile = if (defaultSelectedLocation != null) {
        profiles.firstOrNull { it.name.equals(defaultSelectedLocation, ignoreCase = true) } ?: profiles.firstOrNull()
    } else {
        profiles.firstOrNull()
    }

    var customerName by remember {
        mutableStateOf(
            initialRecord?.customerName.takeIf { !it.isNullOrEmpty() }
                ?: initialProfile?.name
                ?: "Lokasi 1 (Rumah Utama)"
        )
    }

    var meterNumber by remember {
        mutableStateOf(
            initialRecord?.meterNumber.takeIf { !it.isNullOrEmpty() }
                ?: initialProfile?.plnMeterNumber
                ?: defaultMeter
        )
    }

    var tariffType by remember {
        mutableStateOf(
            initialRecord?.tariffType.takeIf { !it.isNullOrEmpty() }
                ?: initialProfile?.plnTariffType
                ?: "R-1/1300 VA"
        )
    }

    var nominalText by remember {
        mutableStateOf(initialRecord?.nominal?.toLong()?.toString() ?: "50000")
    }
    var kwhText by remember {
        val initialKwh = initialRecord?.kwhReceived
        if (initialKwh != null) {
            mutableStateOf(initialKwh.toString())
        } else {
            val matchingPreset = presets.firstOrNull { it.nominal == 50000.0 }
            mutableStateOf(matchingPreset?.kwhReceived?.toString() ?: "33.2")
        }
    }
    var tokenNumber by remember {
        mutableStateOf(initialRecord?.tokenNumber ?: "")
    }
    var merchant by remember {
        mutableStateOf(initialRecord?.merchant ?: "PLN Mobile")
    }
    var notes by remember {
        mutableStateOf(initialRecord?.notes ?: "")
    }
    var isApplied by remember {
        mutableStateOf(initialRecord?.isAppliedToMeter ?: true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(26.dp)),
            color = BentoCardBg,
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricGoldSubtle)
                                .border(1.dp, ElectricGoldBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = ElectricGoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (initialRecord == null) "Catat Token Listrik" else "Edit Catatan Token",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pilih lokasi meteran & masukkan nominal",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMutedDark
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_electricity_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location Selector Bento Section
                Text(
                    text = "PILIH LOKASI CATATAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = ElectricGoldSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profiles.forEach { profile ->
                        val isSelected = customerName.equals(profile.name, ignoreCase = true)
                        val icon = when {
                            profile.name.contains("kontrakan", ignoreCase = true) || profile.name.contains("kost", ignoreCase = true) || profile.name.contains("2", ignoreCase = true) -> Icons.Default.Apartment
                            else -> Icons.Default.Home
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) ElectricGoldSubtle else BentoTileInner,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricGoldBorder else BentoBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    customerName = profile.name
                                    if (profile.plnMeterNumber.isNotEmpty()) {
                                        meterNumber = profile.plnMeterNumber
                                    }
                                    if (profile.plnTariffType.isNotEmpty()) {
                                        tariffType = profile.plnTariffType
                                    }
                                }
                                .testTag("dialog_location_chip_${profile.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) ElectricGoldPrimary else TextMutedDark,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = profile.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) ElectricGoldPrimary else TextSecondaryDark
                                    )
                                    val option = com.example.data.local.PlnTariffHelper.getOptionByCode(profile.plnTariffType)
                                    Text(
                                        text = "${if (profile.plnMeterNumber.isNotBlank()) profile.plnMeterNumber else "PLN"} • ${option.powerLabel}",
                                        fontSize = 9.sp,
                                        color = if (isSelected) ElectricGoldSecondary else TextMutedDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker Bento Section (Allows recording for past days)
                Text(
                    text = "TANGGAL TRANSAKSI / PEMBELIAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = ElectricGoldSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BentoTileInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = selectedDateMillis
                            }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    }
                                    selectedDateMillis = newCal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .testTag("electricity_date_picker_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricGoldSubtle)
                                    .border(1.dp, ElectricGoldBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditCalendar,
                                    contentDescription = "Pilih Tanggal",
                                    tint = ElectricGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = UtilityViewModel.formatDateShort(selectedDateMillis),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Ketuk untuk catat tanggal lampau",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TextMutedDark
                                )
                            }
                        }

                        // Quick buttons: Hari Ini & Kemarin
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricGoldSubtle,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedDateMillis = System.currentTimeMillis()
                                    }
                            ) {
                                Text(
                                    text = "Hari Ini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricGoldPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BentoCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedDateMillis = System.currentTimeMillis() - 86400000L
                                    }
                            ) {
                                Text(
                                    text = "Kemarin",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Presets Selection Bar (50k, 100k, etc.)
                Text(
                    text = "PILIH NOMINAL CEPAT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = ElectricGoldSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.distinctBy { it.nominal }.forEach { preset ->
                        val isSelected = nominalText == preset.nominal.toLong().toString()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricGoldPrimary else BentoTileInner,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricGoldSecondary else BentoBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    nominalText = preset.nominal.toLong().toString()
                                    kwhText = preset.kwhReceived.toString()
                                }
                                .testTag("preset_chip_${preset.nominal.toLong()}")
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = UtilityViewModel.formatRupiah(preset.nominal),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color(0xFF1E1B00) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${preset.kwhReceived} kWh",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) Color(0xFF422006) else TextMutedDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nominal and kWh Inputs (Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nominalText,
                        onValueChange = { input ->
                            nominalText = input.filter { it.isDigit() }
                            val nominalVal = nominalText.toDoubleOrNull() ?: 0.0
                            val calculatedKwh = onEstimateKwh(nominalVal)
                            if (calculatedKwh > 0) {
                                kwhText = calculatedKwh.toString()
                            }
                        },
                        label = { Text("Nominal (Rp)", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("electricity_nominal_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = kwhText,
                        onValueChange = { kwhText = it },
                        label = { Text("Dapat (kWh)", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("electricity_kwh_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 20 Digit Token PLN
                OutlinedTextField(
                    value = tokenNumber,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }.take(20)
                        tokenNumber = digitsOnly
                    },
                    label = { Text("20 Digit Nomor Token PLN (Stroom)", color = TextMutedDark, fontSize = 12.sp) },
                    placeholder = { Text("Contoh: 12345678901234567890", color = TextMutedDark) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("electricity_token_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoTileInner,
                        unfocusedContainerColor = BentoTileInner,
                        focusedBorderColor = ElectricGoldBorder,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ID Pelanggan / No. Meter & Daya
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = meterNumber,
                        onValueChange = { meterNumber = it },
                        label = { Text("No. Meter / ID PLN", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("electricity_meter_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = tariffType,
                        onValueChange = { tariffType = it },
                        label = { Text("Tarif/Daya", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("electricity_tariff_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Merchant & Lokasi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = merchant,
                        onValueChange = { merchant = it },
                        label = { Text("Beli Melalui", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("electricity_merchant_input"),
                        placeholder = { Text("BCA, Indomaret, dll", color = TextMutedDark) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Lokasi", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("electricity_location_input"),
                        placeholder = { Text("Lokasi 1 (Rumah Utama)", color = TextMutedDark) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = ElectricGoldBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Opsional)", color = TextMutedDark, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("electricity_notes_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoTileInner,
                        unfocusedContainerColor = BentoTileInner,
                        focusedBorderColor = ElectricGoldBorder,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Switch status: Sudah dimasukkan ke meteran
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = BentoTileInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sudah Diinput ke Meteran",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isApplied) "Token sudah terisi di kWh meter" else "Token masih menunggu diinput",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMutedDark
                            )
                        }

                        Switch(
                            checked = isApplied,
                            onCheckedChange = { isApplied = it },
                            modifier = Modifier.testTag("electricity_applied_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricGoldPrimary,
                                checkedTrackColor = ElectricGoldSubtle
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                } // End scrollable column

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_electricity_button")
                    ) {
                        Text("Batal", color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val nominalVal = nominalText.toDoubleOrNull() ?: 50000.0
                            val kwhVal = kwhText.toDoubleOrNull() ?: 33.2
                            val record = initialRecord?.copy(
                                dateEpochMillis = selectedDateMillis,
                                nominal = nominalVal,
                                kwhReceived = kwhVal,
                                tokenNumber = tokenNumber,
                                meterNumber = meterNumber,
                                customerName = customerName,
                                tariffType = tariffType,
                                merchant = merchant,
                                notes = notes,
                                isAppliedToMeter = isApplied
                            ) ?: ElectricityRecord(
                                dateEpochMillis = selectedDateMillis,
                                nominal = nominalVal,
                                kwhReceived = kwhVal,
                                tokenNumber = tokenNumber,
                                meterNumber = meterNumber,
                                customerName = customerName,
                                tariffType = tariffType,
                                merchant = merchant,
                                notes = notes,
                                isAppliedToMeter = isApplied
                            )
                            onSave(record)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_electricity_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricGoldPrimary,
                            contentColor = Color(0xFF1E1B00)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (initialRecord == null) "Simpan Pembelian" else "Perbarui Data",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

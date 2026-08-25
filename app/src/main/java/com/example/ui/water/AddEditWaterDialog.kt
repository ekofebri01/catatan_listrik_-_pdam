package com.example.ui.water

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.local.entity.UtilityConfig
import com.example.data.local.entity.WaterRecord
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessBorder
import com.example.ui.theme.EmeraldSuccessSubtle
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WaterCyanBorder
import com.example.ui.theme.WaterCyanLight
import com.example.ui.theme.WaterCyanPrimary
import com.example.ui.theme.WaterCyanSecondary
import com.example.ui.theme.WaterCyanSubtle
import com.example.ui.viewmodel.UtilityViewModel
import java.util.Calendar

@Composable
fun AddEditWaterDialog(
    initialRecord: WaterRecord? = null,
    latestRecord: WaterRecord? = null,
    config: UtilityConfig,
    profiles: List<CustomerProfile>,
    defaultSelectedLocation: String? = null,
    onGetLatestForLocation: ((String) -> WaterRecord?)? = null,
    onDismiss: () -> Unit,
    onSave: (WaterRecord) -> Unit,
    onCalculateBill: (Double) -> Double
) {
    val context = LocalContext.current
    val currentCal = Calendar.getInstance()
    var selectedDateMillis by remember {
        mutableStateOf(initialRecord?.readingDateEpochMillis ?: System.currentTimeMillis())
    }
    var selectedMonth by remember {
        mutableStateOf(initialRecord?.periodMonth ?: (currentCal.get(Calendar.MONTH) + 1))
    }
    var selectedYear by remember {
        mutableStateOf(initialRecord?.periodYear ?: currentCal.get(Calendar.YEAR))
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
                ?: config.defaultCustomerName
        )
    }

    val locLatest = if (initialRecord == null && onGetLatestForLocation != null) {
        onGetLatestForLocation(customerName) ?: latestRecord
    } else {
        latestRecord
    }

    var prevStandText by remember {
        val stand = initialRecord?.previousMeterReading
            ?: locLatest?.currentMeterReading
            ?: 0.0
        mutableStateOf(if (stand > 0) stand.toString() else "0")
    }

    var currentStandText by remember {
        val stand = initialRecord?.currentMeterReading
            ?: ((locLatest?.currentMeterReading ?: 0.0) + 15.0)
        mutableStateOf(if (stand > 0) stand.toString() else "15")
    }

    var customEstimatedBillText by remember {
        val initialEst = initialRecord?.estimatedBillAmount
        if (initialEst != null) {
            mutableStateOf(initialEst.toLong().toString())
        } else {
            val prev = prevStandText.toDoubleOrNull() ?: 0.0
            val curr = currentStandText.toDoubleOrNull() ?: 0.0
            val usage = if (curr >= prev) curr - prev else 0.0
            val est = onCalculateBill(usage)
            mutableStateOf(est.toLong().toString())
        }
    }

    var isPaid by remember {
        mutableStateOf(initialRecord?.isPaid ?: false)
    }

    var actualPaidText by remember {
        mutableStateOf(
            if (initialRecord?.actualPaidAmount != null && initialRecord.actualPaidAmount > 0) {
                initialRecord.actualPaidAmount.toLong().toString()
            } else ""
        )
    }

    var meterNumber by remember {
        mutableStateOf(
            initialRecord?.meterNumber.takeIf { !it.isNullOrEmpty() }
                ?: initialProfile?.pdamMeterNumber
                ?: config.defaultPdamMeterNumber
        )
    }

    var pdamName by remember {
        mutableStateOf(
            initialRecord?.pdamRegionName.takeIf { !it.isNullOrEmpty() }
                ?: initialProfile?.pdamName
                ?: config.defaultPdamName
        )
    }

    var notes by remember {
        mutableStateOf(initialRecord?.notes ?: "")
    }

    // Calculated usage m³
    val prevVal = prevStandText.toDoubleOrNull() ?: 0.0
    val currVal = currentStandText.toDoubleOrNull() ?: 0.0
    val calculatedUsage = if (currVal >= prevVal) currVal - prevVal else 0.0

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
                                .background(WaterCyanSubtle)
                                .border(1.dp, WaterCyanBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = WaterCyanSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (initialRecord == null) "Catat Meter Air PDAM" else "Edit Catatan PDAM",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Isi stand meter & edit perkiraan tagihan",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMutedDark
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_water_dialog_button")
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
                    color = WaterCyanSecondary
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
                            color = if (isSelected) WaterCyanSubtle else BentoTileInner,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) WaterCyanBorder else BentoBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    customerName = profile.name
                                    if (profile.pdamMeterNumber.isNotEmpty()) {
                                        meterNumber = profile.pdamMeterNumber
                                    }
                                    if (profile.pdamName.isNotEmpty()) {
                                        pdamName = profile.pdamName
                                    }
                                    // Update stand meter from this location's history
                                    if (initialRecord == null && onGetLatestForLocation != null) {
                                        val latestForLoc = onGetLatestForLocation(profile.name)
                                        val prevStand = latestForLoc?.currentMeterReading ?: 0.0
                                        prevStandText = if (prevStand > 0) prevStand.toString() else "0"
                                        val currStand = (prevStand + 15.0)
                                        currentStandText = currStand.toString()
                                        val est = onCalculateBill(15.0)
                                        customEstimatedBillText = est.toLong().toString()
                                    }
                                }
                                .testTag("dialog_water_location_chip_${profile.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) WaterCyanSecondary else TextMutedDark,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = profile.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) WaterCyanSecondary else TextSecondaryDark
                                    )
                                    if (profile.pdamMeterNumber.isNotEmpty()) {
                                        Text(
                                            text = profile.pdamMeterNumber,
                                            fontSize = 9.sp,
                                            color = if (isSelected) WaterCyanLight else TextMutedDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker Bento Section (Allows recording for past days)
                Text(
                    text = "TANGGAL PENCATATAN STAND METER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = WaterCyanSecondary
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
                                    selectedMonth = month + 1
                                    selectedYear = year
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .testTag("water_date_picker_card")
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
                                    .background(WaterCyanSubtle)
                                    .border(1.dp, WaterCyanBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditCalendar,
                                    contentDescription = "Pilih Tanggal",
                                    tint = WaterCyanSecondary,
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
                                color = WaterCyanSubtle,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val now = System.currentTimeMillis()
                                        selectedDateMillis = now
                                        val nowCal = Calendar.getInstance()
                                        selectedMonth = nowCal.get(Calendar.MONTH) + 1
                                        selectedYear = nowCal.get(Calendar.YEAR)
                                    }
                            ) {
                                Text(
                                    text = "Hari Ini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterCyanSecondary,
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
                                        val yest = System.currentTimeMillis() - 86400000L
                                        selectedDateMillis = yest
                                        val yestCal = Calendar.getInstance().apply { timeInMillis = yest }
                                        selectedMonth = yestCal.get(Calendar.MONTH) + 1
                                        selectedYear = yestCal.get(Calendar.YEAR)
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

                // Period Info (Bulan & Tahun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = "${UtilityViewModel.getMonthName(selectedMonth)} $selectedYear",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Periode Tagihan", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_period_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = WaterCyanBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stand Meter Awal & Stand Meter Akhir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = prevStandText,
                        onValueChange = { input ->
                            prevStandText = input
                            val p = input.toDoubleOrNull() ?: 0.0
                            val c = currentStandText.toDoubleOrNull() ?: 0.0
                            val u = if (c >= p) c - p else 0.0
                            val autoEst = onCalculateBill(u)
                            customEstimatedBillText = autoEst.toLong().toString()
                        },
                        label = { Text("Stand Awal (m³)", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_prev_meter_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = WaterCyanBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = currentStandText,
                        onValueChange = { input ->
                            currentStandText = input
                            val p = prevStandText.toDoubleOrNull() ?: 0.0
                            val c = input.toDoubleOrNull() ?: 0.0
                            val u = if (c >= p) c - p else 0.0
                            val autoEst = onCalculateBill(u)
                            customEstimatedBillText = autoEst.toLong().toString()
                        },
                        label = { Text("Stand Akhir (m³)", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_current_meter_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = WaterCyanBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculated Usage & Live Tariff Preview Bento Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BentoTileInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PEMAKAIAN AIR BULAN INI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = WaterCyanSecondary
                            )
                            Text(
                                text = UtilityViewModel.formatM3(calculatedUsage),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TARIF BERLAKU",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = TextMutedDark
                            )
                            Text(
                                text = if (config.isTierPricingEnabled) "Tarif Bertingkat PDAM" else "@ ${UtilityViewModel.formatRupiah(config.defaultWaterRatePerM3)}/m³",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                                color = WaterCyanLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Editable Perkiraan Tagihan Air (Rp)
                OutlinedTextField(
                    value = customEstimatedBillText,
                    onValueChange = { input ->
                        customEstimatedBillText = input.filter { it.isDigit() }
                    },
                    label = { Text("Perkiraan Tagihan Air (Rp) - Dapat Diedit", color = TextMutedDark, fontSize = 12.sp) },
                    placeholder = { Text("Estimasi total tagihan", color = TextMutedDark) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("water_estimated_bill_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoTileInner,
                        unfocusedContainerColor = BentoTileInner,
                        focusedBorderColor = WaterCyanBorder,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ID Pelanggan & Nama PDAM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = meterNumber,
                        onValueChange = { meterNumber = it },
                        label = { Text("No. Sambungan PDAM", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("water_meter_number_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = WaterCyanBorder,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = pdamName,
                        onValueChange = { pdamName = it },
                        label = { Text("Nama PDAM", color = TextMutedDark, fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("water_pdam_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoTileInner,
                            unfocusedContainerColor = BentoTileInner,
                            focusedBorderColor = WaterCyanBorder,
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
                    label = { Text("Catatan (Opsional)", color = TextMutedDark, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("water_notes_input"),
                    placeholder = { Text("Misal: Meteran dibaca tgl 20", color = TextMutedDark) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoTileInner,
                        unfocusedContainerColor = BentoTileInner,
                        focusedBorderColor = WaterCyanBorder,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status Pembayaran (Lunas / Belum)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = BentoTileInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isPaid) "Status: Sudah Lunas" else "Status: Belum Dibayar",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                    color = if (isPaid) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isPaid) "Tagihan sudah dibayarkan" else "Tagihan masih menunggu pembayaran",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TextMutedDark
                                )
                            }

                            Switch(
                                checked = isPaid,
                                onCheckedChange = { isPaid = it },
                                modifier = Modifier.testTag("water_is_paid_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldSuccess,
                                    checkedTrackColor = EmeraldSuccessSubtle
                                )
                            )
                        }

                        if (isPaid) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = actualPaidText,
                                onValueChange = { actualPaidText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Nominal Riil yang Dibayar (Rp)", color = TextMutedDark, fontSize = 12.sp) },
                                placeholder = { Text(customEstimatedBillText, color = TextMutedDark) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("water_actual_paid_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BentoCardBg,
                                    unfocusedContainerColor = BentoCardBg,
                                    focusedBorderColor = EmeraldSuccessBorder,
                                    unfocusedBorderColor = BentoBorder,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
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
                        modifier = Modifier.testTag("cancel_water_button")
                    ) {
                        Text("Batal", color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val prevM = prevStandText.toDoubleOrNull() ?: 0.0
                            val currM = currentStandText.toDoubleOrNull() ?: 0.0
                            val usageM = if (currM >= prevM) currM - prevM else 0.0
                            val estBill = customEstimatedBillText.toDoubleOrNull() ?: onCalculateBill(usageM)
                            val actPaid = if (isPaid) (actualPaidText.toDoubleOrNull() ?: estBill) else 0.0

                            val record = initialRecord?.copy(
                                periodMonth = selectedMonth,
                                periodYear = selectedYear,
                                readingDateEpochMillis = selectedDateMillis,
                                previousMeterReading = prevM,
                                currentMeterReading = currM,
                                usageM3 = usageM,
                                baseFee = config.defaultWaterBaseFee,
                                ratePerM3 = config.defaultWaterRatePerM3,
                                adminFee = config.defaultWaterAdminFee,
                                maintenanceFee = config.defaultWaterMaintenanceFee,
                                estimatedBillAmount = estBill,
                                actualPaidAmount = actPaid,
                                isPaid = isPaid,
                                paidDateEpochMillis = if (isPaid) (initialRecord.paidDateEpochMillis ?: System.currentTimeMillis()) else null,
                                meterNumber = meterNumber,
                                pdamRegionName = pdamName,
                                customerName = customerName,
                                notes = notes
                            ) ?: WaterRecord(
                                periodMonth = selectedMonth,
                                periodYear = selectedYear,
                                readingDateEpochMillis = selectedDateMillis,
                                previousMeterReading = prevM,
                                currentMeterReading = currM,
                                usageM3 = usageM,
                                baseFee = config.defaultWaterBaseFee,
                                ratePerM3 = config.defaultWaterRatePerM3,
                                adminFee = config.defaultWaterAdminFee,
                                maintenanceFee = config.defaultWaterMaintenanceFee,
                                estimatedBillAmount = estBill,
                                actualPaidAmount = actPaid,
                                isPaid = isPaid,
                                paidDateEpochMillis = if (isPaid) System.currentTimeMillis() else null,
                                meterNumber = meterNumber,
                                pdamRegionName = pdamName,
                                customerName = customerName,
                                notes = notes
                            )

                            onSave(record)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_water_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WaterCyanPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (initialRecord == null) "Simpan Catatan PDAM" else "Perbarui Data",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


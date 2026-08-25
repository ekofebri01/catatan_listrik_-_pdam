package com.example.ui.water

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WaterRecord
import com.example.ui.components.ExecutiveHeroMetricCard
import com.example.ui.components.LocationBadge
import com.example.ui.components.LocationSelectorBar
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessBorder
import com.example.ui.theme.EmeraldSuccessSubtle
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.RoseAlertBorder
import com.example.ui.theme.RoseAlertSubtle
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WaterCyanBorder
import com.example.ui.theme.WaterCyanLight
import com.example.ui.theme.WaterCyanPrimary
import com.example.ui.theme.WaterCyanSecondary
import com.example.ui.theme.WaterCyanSubtle
import com.example.ui.viewmodel.UtilityUiState
import com.example.ui.viewmodel.UtilityViewModel

@Composable
fun WaterScreen(
    uiState: UtilityUiState,
    viewModel: UtilityViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<WaterRecord?>(null) }
    var deletingRecord by remember { mutableStateOf<WaterRecord?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecords = remember(uiState.waterRecords, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.waterRecords
        } else {
            uiState.waterRecords.filter {
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                        it.meterNumber.contains(searchQuery, ignoreCase = true) ||
                        it.pdamRegionName.contains(searchQuery, ignoreCase = true) ||
                        it.notes.contains(searchQuery, ignoreCase = true) ||
                        UtilityViewModel.getMonthName(it.periodMonth).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("water_record_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Metric Bento Card
            item {
                ExecutiveHeroMetricCard(
                    title = "Tagihan & Meter Air PDAM",
                    primaryValue = UtilityViewModel.formatRupiah(uiState.totalWaterBillEstimatedThisMonth),
                    secondaryValue = UtilityViewModel.formatM3(uiState.totalWaterUsageThisMonth),
                    primarySublabel = "Total Tagihan",
                    secondarySublabel = "Pemakaian Air",
                    icon = Icons.Default.WaterDrop,
                    isElectric = false,
                    timeFilterLabel = if (uiState.selectedTimeFilter == com.example.ui.viewmodel.TimeFilter.THIS_MONTH) "Bulan Ini" else "Semua Waktu",
                    onTimeFilterClick = { viewModel.toggleTimeFilter() },
                    onActionClick = null,
                    modifier = Modifier.testTag("water_hero_metric_card")
                )
            }

            // Location Selector Pill Bar (Multi-Location Support)
            item {
                LocationSelectorBar(
                    profiles = uiState.profiles,
                    selectedLocation = uiState.selectedLocationFilter,
                    onSelectLocation = { viewModel.setLocationFilter(it) },
                    modifier = Modifier.testTag("water_location_selector_bar")
                )
            }

            // Unpaid Bills Alert Bento Banner
            if (uiState.unpaidWaterBillsCount > 0) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("water_unpaid_alert_banner"),
                        shape = RoundedCornerShape(20.dp),
                        color = RoseAlertSubtle,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseAlertBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RoseAlert.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PendingActions,
                                    contentDescription = null,
                                    tint = RoseAlert,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${uiState.unpaidWaterBillsCount} Tagihan Belum Dibayar",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = RoseAlert
                                )
                                Text(
                                    text = "Selesaikan pembayaran PDAM untuk menghindari denda.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }

            // Quick Search Bento Row
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari bulan, no meter, lokasi, catatan...", color = TextMutedDark, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = TextMutedDark,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("water_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoCardBg,
                        unfocusedContainerColor = BentoCardBg,
                        focusedBorderColor = WaterCyanBorder,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Empty State
            if (filteredRecords.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .testTag("water_empty_state_card")
                            .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(WaterCyanSubtle)
                                    .border(1.dp, WaterCyanBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = WaterCyanSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Tidak Ditemukan" else "Belum Ada Catatan Meteran Air",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Coba kata kunci pencarian lainnya." else "Catat stand meter air Anda untuk menghitung pemakaian m³ & estimasi tagihan otomatis.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Records List
            items(filteredRecords, key = { it.id }) { record ->
                WaterRecordCard(
                    record = record,
                    onEdit = { editingRecord = record },
                    onDelete = { deletingRecord = record },
                    onTogglePaid = { isPaid ->
                        viewModel.markWaterAsPaid(record, isPaid)
                    }
                )
            }

            // Bottom space for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = WaterCyanPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_water_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Catat Meter Air")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Catat Meter Air", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        val latest = uiState.waterRecords.firstOrNull()
        AddEditWaterDialog(
            initialRecord = null,
            latestRecord = latest,
            config = uiState.config,
            profiles = uiState.profiles,
            defaultSelectedLocation = uiState.selectedLocationFilter,
            onGetLatestForLocation = { loc -> viewModel.getLatestWaterRecordForLocation(loc) },
            onDismiss = { showAddDialog = false },
            onSave = { viewModel.addOrUpdateWater(it) },
            onCalculateBill = { viewModel.calculateEstimatedWaterBill(it) }
        )
    }

    // Edit Dialog
    editingRecord?.let { record ->
        AddEditWaterDialog(
            initialRecord = record,
            latestRecord = null,
            config = uiState.config,
            profiles = uiState.profiles,
            defaultSelectedLocation = record.customerName,
            onGetLatestForLocation = { loc -> viewModel.getLatestWaterRecordForLocation(loc) },
            onDismiss = { editingRecord = null },
            onSave = {
                viewModel.addOrUpdateWater(it)
                editingRecord = null
            },
            onCalculateBill = { viewModel.calculateEstimatedWaterBill(it) }
        )
    }

    // Delete Confirmation
    deletingRecord?.let { record ->
        AlertDialog(
            onDismissRequest = { deletingRecord = null },
            title = { Text("Hapus Catatan PDAM?") },
            text = {
                Text("Catatan meteran periode ${UtilityViewModel.getMonthName(record.periodMonth)} ${record.periodYear} akan dihapus.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWater(record)
                        deletingRecord = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlert),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_delete_water_button")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deletingRecord = null },
                    modifier = Modifier.testTag("cancel_delete_water_button")
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun WaterRecordCard(
    record: WaterRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePaid: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            .testTag("water_card_${record.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Periode + Tagihan Status Bento Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${UtilityViewModel.getMonthName(record.periodMonth)} ${record.periodYear}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Dicatat: ${UtilityViewModel.formatDateShort(record.readingDateEpochMillis)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextMutedDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (record.isPaid) EmeraldSuccessSubtle else RoseAlertSubtle,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (record.isPaid) EmeraldSuccessBorder else RoseAlertBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTogglePaid(!record.isPaid) }
                        .testTag("toggle_paid_water_${record.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (record.isPaid) Icons.Default.CheckCircle else Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = if (record.isPaid) EmeraldSuccess else RoseAlert,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (record.isPaid) "LUNAS" else "BELUM BAYAR",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (record.isPaid) EmeraldSuccess else RoseAlert
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Meter Reading Visual Gauge (Stand Awal -> Stand Akhir = Pemakaian)
            Surface(
                shape = RoundedCornerShape(16.dp),
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
                            text = "STAND AWAL",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                            color = TextMutedDark
                        )
                        Text(
                            text = "${record.previousMeterReading} m³",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "➔",
                        style = MaterialTheme.typography.titleMedium,
                        color = WaterCyanSecondary
                    )

                    Column {
                        Text(
                            text = "STAND AKHIR",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                            color = TextMutedDark
                        )
                        Text(
                            text = "${record.currentMeterReading} m³",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(BentoBorder)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "PEMAKAIAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = WaterCyanSecondary
                        )
                        Text(
                            text = UtilityViewModel.formatM3(record.usageM3),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = WaterCyanLight
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Estimated Total / Paid Amount Bento Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (record.isPaid && record.actualPaidAmount > 0) "TOTAL DIBAYAR" else "PERKIRAAN TAGIHAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextMutedDark
                    )
                    Text(
                        text = UtilityViewModel.formatRupiah(
                            if (record.isPaid && record.actualPaidAmount > 0) record.actualPaidAmount else record.estimatedBillAmount
                        ),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = if (record.isPaid) EmeraldSuccess else WaterCyanSecondary
                        )
                    )
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("edit_water_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("delete_water_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = RoseAlert,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Location badge & Connection number
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (record.customerName.isNotEmpty()) {
                    LocationBadge(
                        locationName = record.customerName,
                        isElectric = false
                    )
                }
                if (record.meterNumber.isNotEmpty() || record.pdamRegionName.isNotEmpty()) {
                    Text(
                        text = "No. ${if (record.meterNumber.isNotEmpty()) record.meterNumber else "-"} • ${record.pdamRegionName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextMutedDark
                    )
                }
            }

            if (record.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan: ${record.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 11.sp
                    ),
                    color = TextMutedDark
                )
            }
        }
    }
}

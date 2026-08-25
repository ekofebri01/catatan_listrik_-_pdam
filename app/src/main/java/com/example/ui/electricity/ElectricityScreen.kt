package com.example.ui.electricity

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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Search
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
import com.example.data.local.entity.ElectricityRecord
import com.example.ui.components.ExecutiveHeroMetricCard
import com.example.ui.components.LocationBadge
import com.example.ui.components.LocationSelectorBar
import com.example.ui.components.TokenDisplayCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSecondary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.RoseAlertBorder
import com.example.ui.theme.RoseAlertSubtle
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.UtilityUiState
import com.example.ui.viewmodel.UtilityViewModel

@Composable
fun ElectricityScreen(
    uiState: UtilityUiState,
    viewModel: UtilityViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<ElectricityRecord?>(null) }
    var deletingRecord by remember { mutableStateOf<ElectricityRecord?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecords = remember(uiState.electricityRecords, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.electricityRecords
        } else {
            uiState.electricityRecords.filter {
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.dateEpochMillis }
                val monthName = java.text.SimpleDateFormat("MMMM", java.util.Locale("id", "ID")).format(cal.time)
                
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                        it.meterNumber.contains(searchQuery, ignoreCase = true) ||
                        it.tokenNumber.contains(searchQuery, ignoreCase = true) ||
                        it.merchant.contains(searchQuery, ignoreCase = true) ||
                        it.notes.contains(searchQuery, ignoreCase = true) ||
                        monthName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("electricity_record_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Bento Metric Card
            item {
                ExecutiveHeroMetricCard(
                    title = "Listrik PLN",
                    primaryValue = UtilityViewModel.formatRupiah(uiState.totalElectricitySpentThisMonth),
                    secondaryValue = UtilityViewModel.formatKwh(uiState.totalKwhThisMonth),
                    primarySublabel = "Total Biaya",
                    secondarySublabel = "Energi Masuk",
                    icon = Icons.Default.ElectricBolt,
                    isElectric = true,
                    timeFilterLabel = if (uiState.selectedTimeFilter == com.example.ui.viewmodel.TimeFilter.THIS_MONTH) "Bulan Ini" else "Semua Waktu",
                    onTimeFilterClick = { viewModel.toggleTimeFilter() },
                    onActionClick = null,
                    modifier = Modifier.testTag("electricity_hero_metric_card")
                )
            }

            // Location Selector Pill Bar (Multi-Location Support)
            item {
                LocationSelectorBar(
                    profiles = uiState.profiles,
                    selectedLocation = uiState.selectedLocationFilter,
                    onSelectLocation = { viewModel.setLocationFilter(it) },
                    modifier = Modifier.testTag("electricity_location_selector_bar")
                )
            }

            // Quick Search Bento Row
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari bulan, token, ID meter, catatan...", color = TextMutedDark, fontSize = 13.sp) },
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
                        .testTag("electricity_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoCardBg,
                        unfocusedContainerColor = BentoCardBg,
                        focusedBorderColor = ElectricGoldBorder,
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
                            .testTag("electricity_empty_state_card")
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
                                .background(ElectricGoldSubtle)
                                .border(1.dp, ElectricGoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = ElectricGoldPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Tidak Ditemukan" else "Belum Ada Riwayat Token",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Coba kata kunci lain atau hapus pencarian." else "Klik 'Catat Token' untuk menambahkan histori pembelian listrik PLN.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Bento Records List
            items(filteredRecords, key = { it.id }) { record ->
                ElectricityRecordCard(
                    record = record,
                    onEdit = { editingRecord = record },
                    onDelete = { deletingRecord = record }
                )
            }

            // Bottom space for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Button in Bento Accent
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = ElectricGoldPrimary,
            contentColor = Color(0xFF1E1B00),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_electricity_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Beli Token")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Catat Token", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        AddEditElectricityDialog(
            initialRecord = null,
            presets = uiState.presets,
            profiles = uiState.profiles,
            defaultSelectedLocation = uiState.selectedLocationFilter,
            defaultMeter = uiState.config.defaultPlnMeterNumber,
            onDismiss = { showAddDialog = false },
            onSave = { viewModel.addOrUpdateElectricity(it) },
            onEstimateKwh = { viewModel.estimateKwhForNominal(it) }
        )
    }

    // Edit Dialog
    editingRecord?.let { record ->
        AddEditElectricityDialog(
            initialRecord = record,
            presets = uiState.presets,
            profiles = uiState.profiles,
            defaultSelectedLocation = record.customerName,
            defaultMeter = uiState.config.defaultPlnMeterNumber,
            onDismiss = { editingRecord = null },
            onSave = {
                viewModel.addOrUpdateElectricity(it)
                editingRecord = null
            },
            onEstimateKwh = { viewModel.estimateKwhForNominal(it) }
        )
    }

    // Delete Confirmation
    deletingRecord?.let { record ->
        AlertDialog(
            onDismissRequest = { deletingRecord = null },
            title = { Text("Hapus Catatan Listrik?") },
            text = {
                Text("Catatan pembelian token ${UtilityViewModel.formatRupiah(record.nominal)} (${record.kwhReceived} kWh) akan dihapus secara permanen.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteElectricity(record)
                        deletingRecord = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlert),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_delete_electricity_button")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deletingRecord = null },
                    modifier = Modifier.testTag("cancel_delete_electricity_button")
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ElectricityRecordCard(
    record: ElectricityRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            .testTag("electricity_card_${record.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Nominal + Date & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = UtilityViewModel.formatRupiah(record.nominal),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ElectricGoldPrimary
                        )
                    )
                    Text(
                        text = UtilityViewModel.formatDate(record.dateEpochMillis),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextMutedDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ElectricGoldSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGoldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = ElectricGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${record.kwhReceived} kWh",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = ElectricGoldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Token Number (if available)
            if (record.tokenNumber.isNotBlank()) {
                TokenDisplayCard(tokenNumber = record.tokenNumber)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Meter info & Merchant Bento Sub-box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BentoTileInner,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (record.customerName.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LocationBadge(
                                    locationName = record.customerName,
                                    isElectric = true
                                )
                                if (record.merchant.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${record.merchant}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = TextMutedDark
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = "ID: ${if (record.meterNumber.isNotEmpty()) record.meterNumber else "-"} • ${record.tariffType}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TextSecondaryDark
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("edit_electricity_${record.id}")
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
                                .size(32.dp)
                                .testTag("delete_electricity_${record.id}")
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
            }

            if (record.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
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

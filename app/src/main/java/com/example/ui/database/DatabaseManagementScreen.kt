package com.example.ui.database

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSecondary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseAlert
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
fun DatabaseManagementScreen(
    uiState: UtilityUiState,
    viewModel: UtilityViewModel
) {
    var showAddPresetDialog by remember { mutableStateOf(false) }
    var editingPreset by remember { mutableStateOf<ElectricityPreset?>(null) }
    var deletingPreset by remember { mutableStateOf<ElectricityPreset?>(null) }

    var showWaterTariffDialog by remember { mutableStateOf(false) }

    var showAddProfileDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<CustomerProfile?>(null) }
    var deletingProfile by remember { mutableStateOf<CustomerProfile?>(null) }
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            viewModel.exportBackup(context, uri)
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.importBackup(context, uri)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("database_management_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Header Info Bento Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Database & Konfigurasi Tarif",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kelola rasio token listrik, rumus PDAM & profil meteran",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Electricity Presets (50k -> X kWh, 100k -> Y kWh)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = ElectricGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Preset Token Listrik",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ElectricGoldSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGoldBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showAddPresetDialog = true }
                                .testTag("add_preset_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = ElectricGoldPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricGoldPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Edit perbandingan nominal pembelian dan kWh didapat (misal 50k dpt 33.2 kWh, 100k dpt 66.8 kWh):",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMutedDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.presets.distinctBy { it.nominal }.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BentoTileInner,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("preset_row_${preset.id}")
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
                                            text = UtilityViewModel.formatRupiah(preset.nominal),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = ElectricGoldPrimary,
                                                fontSize = 15.sp
                                            )
                                        )
                                        Text(
                                            text = "Mendapat: ${preset.kwhReceived} kWh ${if (preset.label.isNotEmpty()) "• ${preset.label}" else ""}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = TextSecondaryDark
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { editingPreset = preset },
                                            modifier = Modifier
                                                .size(30.dp)
                                                .testTag("edit_preset_${preset.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = TextSecondaryDark,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { deletingPreset = preset },
                                            modifier = Modifier
                                                .size(30.dp)
                                                .testTag("delete_preset_${preset.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Hapus",
                                                tint = RoseAlert,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Water Tariff Configuration Bento Card
        item {
            val cfg = uiState.config
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = WaterCyanSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Konfigurasi Tarif PDAM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = WaterCyanSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WaterCyanBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showWaterTariffDialog = true }
                                .testTag("edit_water_tariff_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = WaterCyanSecondary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Atur Tarif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WaterCyanSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BentoTileInner,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biaya Beban / Abonemen", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = TextSecondaryDark)
                                Text(UtilityViewModel.formatRupiah(cfg.defaultWaterBaseFee), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Biaya Administrasi PDAM", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = TextSecondaryDark)
                                Text(UtilityViewModel.formatRupiah(cfg.defaultWaterAdminFee), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pemeliharaan Meter / Retribusi", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = TextSecondaryDark)
                                Text(UtilityViewModel.formatRupiah(cfg.defaultWaterMaintenanceFee), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Model Perhitungan", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = TextSecondaryDark)
                                Text(
                                    if (cfg.isTierPricingEnabled) "Tarif Bertingkat (Blok 1, 2, 3)" else "Flat @ ${UtilityViewModel.formatRupiah(cfg.defaultWaterRatePerM3)}/m³",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = WaterCyanLight, fontSize = 13.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Customer Profile Management Bento Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Profil Lokasi & Meteran",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showAddProfileDialog = true }
                                .testTag("add_profile_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.profiles.forEach { profile ->
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(profile.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp))
                                        val option = com.example.data.local.PlnTariffHelper.getOptionByCode(profile.plnTariffType)
                                        val rateText = if (profile.plnTariffType == "CUSTOM") "Rp ${profile.customRatePerKwh}/kWh" else "Rp ${option.ratePerKwh}/kWh"
                                        Text("PLN: ${if (profile.plnMeterNumber.isNotBlank()) profile.plnMeterNumber else "-"} • ${option.name} ($rateText)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = TextSecondaryDark)
                                        if (profile.pdamMeterNumber.isNotEmpty()) {
                                            Text("PDAM: ${profile.pdamMeterNumber} (${profile.pdamName})", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = TextSecondaryDark)
                                        }
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { editingProfile = profile },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondaryDark, modifier = Modifier.size(15.dp))
                                        }
                                        if (uiState.profiles.size > 1) {
                                            IconButton(
                                                onClick = { deletingProfile = profile },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = RoseAlert, modifier = Modifier.size(15.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Cloud Sync / Backup Bento Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cadangkan & Sinkronisasi Data",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Data disimpan secara lokal di Room SQLite Database. Anda juga dapat mencadangkan catatan pembelian dan tarif ke Cloud Firestore.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMutedDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { exportLauncher.launch("VoltHydro_Backup.json") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Text("Ekspor (Lokal)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { importLauncher.launch(arrayOf("application/json")) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Text("Impor (Lokal)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Pilih Ekspor untuk mencadangkan (timpa file lama) atau Impor untuk memulihkan.", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, modifier = Modifier.padding(top = 8.dp, bottom = 14.dp))

                    if (uiState.currentUserEmail == null) {
                        Button(
                            onClick = { viewModel.signInWithGoogle() },
                            enabled = !uiState.isSigningIn,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            if (uiState.isSigningIn) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onSecondary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Login ke Akun Google...")
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Login dengan Google", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "Masuk sebagai: ${uiState.currentUserEmail}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.syncToCloud() },
                            enabled = !uiState.isSyncing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sync_cloud_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Menyinkronkan...")
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sinkronkan Database Sekarang", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (uiState.syncMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BentoTileInner,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.syncMessage ?: "",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.dismissSyncMessage() }) {
                                    Text("Tutup", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Preset Dialogs
    if (showAddPresetDialog) {
        AddEditPresetDialog(
            preset = null,
            onDismiss = { showAddPresetDialog = false },
            onSave = { viewModel.addOrUpdatePreset(it) }
        )
    }

    editingPreset?.let { preset ->
        AddEditPresetDialog(
            preset = preset,
            onDismiss = { editingPreset = null },
            onSave = {
                viewModel.addOrUpdatePreset(it)
                editingPreset = null
            }
        )
    }

    deletingPreset?.let { preset ->
        AlertDialog(
            onDismissRequest = { deletingPreset = null },
            title = { Text("Hapus Preset Listrik?") },
            text = { Text("Preset ${UtilityViewModel.formatRupiah(preset.nominal)} (${preset.kwhReceived} kWh) akan dihapus.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePreset(preset)
                        deletingPreset = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlert),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_delete_preset_button")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPreset = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Water Tariff Dialog
    if (showWaterTariffDialog) {
        EditWaterTariffDialog(
            config = uiState.config,
            onDismiss = { showWaterTariffDialog = false },
            onSave = { viewModel.saveConfig(it) }
        )
    }

    // Profile Dialogs
    if (showAddProfileDialog) {
        AddEditProfileDialog(
            profile = null,
            onDismiss = { showAddProfileDialog = false },
            onSave = { viewModel.addOrUpdateProfile(it) }
        )
    }

    editingProfile?.let { profile ->
        AddEditProfileDialog(
            profile = profile,
            onDismiss = { editingProfile = null },
            onSave = {
                viewModel.addOrUpdateProfile(it)
                editingProfile = null
            }
        )
    }

    deletingProfile?.let { profile ->
        AlertDialog(
            onDismissRequest = { deletingProfile = null },
            title = { Text("Hapus Profil Lokasi?") },
            text = { Text("Profil '${profile.name}' akan dihapus.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProfile(profile)
                        deletingProfile = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlert),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingProfile = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

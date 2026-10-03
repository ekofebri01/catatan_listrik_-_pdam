package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CustomerProfile
import com.example.data.local.entity.ElectricityPreset
import com.example.data.local.entity.ElectricityRecord
import com.example.data.local.entity.UtilityConfig
import com.example.data.local.entity.WaterRecord
import com.example.data.repository.UtilityRepository
import com.example.data.sync.FirebaseSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class UtilityUiState(
    val electricityRecords: List<ElectricityRecord> = emptyList(),
    val waterRecords: List<WaterRecord> = emptyList(),
    val presets: List<ElectricityPreset> = emptyList(),
    val config: UtilityConfig = UtilityConfig(),
    val profiles: List<CustomerProfile> = emptyList(),
    val selectedLocationFilter: String? = null,
    val selectedTimeFilter: TimeFilter = TimeFilter.ALL_TIME,
    val totalElectricitySpentThisMonth: Double = 0.0,
    val totalKwhThisMonth: Double = 0.0,
    val totalWaterUsageThisMonth: Double = 0.0,
    val totalWaterBillEstimatedThisMonth: Double = 0.0,
    val unpaidWaterBillsCount: Int = 0,
    val estimatedDailyKwh: Double = 0.0,
    val estimatedMonthlyKwh: Double = 0.0,
    val estimatedMonthlyElectricityCost: Double = 0.0,
    val syncMessage: String? = null,
    val isSyncing: Boolean = false,
    val currentUserEmail: String? = null,
    val isSigningIn: Boolean = false
)

enum class TimeFilter { ALL_TIME, THIS_MONTH }

class UtilityViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UtilityRepository(database)
    private val syncManager = FirebaseSyncManager(application, repository)
    private val googleAuthHelper = com.example.data.sync.GoogleAuthHelper(application)

    private val _syncMessage = MutableStateFlow<String?>(null)
    private val _isSyncing = MutableStateFlow(false)
    private val _currentUserEmail = MutableStateFlow(syncManager.currentUser?.email)
    private val _isSigningIn = MutableStateFlow(false)

    // Filter states
    val selectedTab = MutableStateFlow(0) // 0: PLN Listrik, 1: PDAM Air, 2: Database & Tarif
    val selectedMonthFilter = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val selectedYearFilter = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedLocationFilter = MutableStateFlow<String?>(null) // null = Semua Lokasi
    val selectedTimeFilter = MutableStateFlow(TimeFilter.ALL_TIME)

    fun setLocationFilter(location: String?) {
        selectedLocationFilter.value = location
    }
    
    fun toggleTimeFilter() {
        if (selectedTimeFilter.value == TimeFilter.ALL_TIME) {
            selectedTimeFilter.value = TimeFilter.THIS_MONTH
        } else {
            selectedTimeFilter.value = TimeFilter.ALL_TIME
        }
    }

    private data class DataBundle(
        val elRecords: List<ElectricityRecord>,
        val wtRecords: List<WaterRecord>,
        val presets: List<ElectricityPreset>,
        val config: UtilityConfig?,
        val profiles: List<CustomerProfile>
    )

    private val dataFlow = combine(
        combine(
            repository.allElectricityRecords,
            repository.allWaterRecords,
            repository.allPresets,
            repository.config,
            repository.allProfiles
        ) { el, wt, presets, cfg, profiles ->
            DataBundle(el, wt, presets, cfg, profiles)
        },
        selectedLocationFilter,
        selectedTimeFilter
    ) { bundle, locationFilter, timeFilter ->
        val elRecords = bundle.elRecords
        val wtRecords = bundle.wtRecords
        val presets = bundle.presets
        val config = bundle.config
        val profiles = bundle.profiles

        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH) + 1
        val currentYear = currentCalendar.get(Calendar.YEAR)

        val locationFilteredEl = if (locationFilter == null) elRecords else elRecords.filter {
            it.customerName.equals(locationFilter, ignoreCase = true)
        }

        val locationFilteredWt = if (locationFilter == null) wtRecords else wtRecords.filter {
            it.customerName.equals(locationFilter, ignoreCase = true)
        }
        
        val visibleElRecords = if (timeFilter == TimeFilter.ALL_TIME) locationFilteredEl else locationFilteredEl.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.dateEpochMillis }
            cal.get(Calendar.MONTH) + 1 == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }
        
        val visibleWtRecords = if (timeFilter == TimeFilter.ALL_TIME) locationFilteredWt else locationFilteredWt.filter {
            it.periodMonth == currentMonth && it.periodYear == currentYear
        }

        val totalElSpent = visibleElRecords.sumOf { it.nominal }
        val totalKwh = visibleElRecords.sumOf { it.kwhReceived }

        val totalWaterUsage = visibleWtRecords.sumOf { it.usageM3 }
        val totalWaterEst = visibleWtRecords.sumOf { it.estimatedBillAmount }
        val unpaidCount = visibleWtRecords.count { !it.isPaid }

        val dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dailyKwh = if (dayOfMonth > 0) totalKwh / dayOfMonth else 0.0
        val estMonthlyKwh = dailyKwh * daysInMonth
        val dailyCost = if (dayOfMonth > 0) totalElSpent / dayOfMonth else 0.0
        val estMonthlyCost = dailyCost * daysInMonth

        UtilityUiState(
            electricityRecords = visibleElRecords,
            waterRecords = visibleWtRecords,
            presets = presets.distinctBy { it.nominal },
            config = config ?: UtilityConfig(),
            profiles = profiles.distinctBy { it.name.trim().lowercase() },
            selectedLocationFilter = locationFilter,
            selectedTimeFilter = timeFilter,
            totalElectricitySpentThisMonth = totalElSpent,
            totalKwhThisMonth = totalKwh,
            totalWaterUsageThisMonth = totalWaterUsage,
            totalWaterBillEstimatedThisMonth = totalWaterEst,
            unpaidWaterBillsCount = unpaidCount,
            estimatedDailyKwh = dailyKwh,
            estimatedMonthlyKwh = estMonthlyKwh,
            estimatedMonthlyElectricityCost = estMonthlyCost
        )
    }

    val uiState: StateFlow<UtilityUiState> = combine(
        dataFlow,
        _syncMessage,
        _isSyncing,
        _currentUserEmail,
        _isSigningIn
    ) { data, syncMsg, isSync, email, signingIn ->
        data.copy(
            syncMessage = syncMsg,
            isSyncing = isSync,
            currentUserEmail = email,
            isSigningIn = signingIn
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UtilityUiState()
    )

    init {
        viewModelScope.launch {
            AppDatabase.populateInitialData(database)
        }
    }

    // Electricity Actions
    fun addOrUpdateElectricity(record: ElectricityRecord) {
        viewModelScope.launch {
            if (record.id == 0L) {
                repository.insertElectricityRecord(record)
            } else {
                repository.updateElectricityRecord(record)
            }
        }
    }

    fun deleteElectricity(record: ElectricityRecord) {
        viewModelScope.launch {
            repository.deleteElectricityRecord(record)
        }
    }

    // Water Actions
    fun addOrUpdateWater(record: WaterRecord) {
        viewModelScope.launch {
            if (record.id == 0L) {
                repository.insertWaterRecord(record)
            } else {
                repository.updateWaterRecord(record)
            }
        }
    }

    fun deleteWater(record: WaterRecord) {
        viewModelScope.launch {
            repository.deleteWaterRecord(record)
        }
    }

    fun markWaterAsPaid(record: WaterRecord, isPaid: Boolean, actualPaid: Double? = null) {
        viewModelScope.launch {
            val updated = record.copy(
                isPaid = isPaid,
                paidDateEpochMillis = if (isPaid) System.currentTimeMillis() else null,
                actualPaidAmount = if (isPaid) (actualPaid ?: record.estimatedBillAmount) else 0.0
            )
            repository.updateWaterRecord(updated)
        }
    }

    // Preset Actions (e.g. 50k -> X kWh, 100k -> Y kWh)
    fun addOrUpdatePreset(preset: ElectricityPreset) {
        viewModelScope.launch {
            if (preset.id == 0L) {
                repository.insertPreset(preset)
            } else {
                repository.updatePreset(preset)
            }
        }
    }

    fun deletePreset(preset: ElectricityPreset) {
        viewModelScope.launch {
            repository.deletePreset(preset)
        }
    }

    // Config Actions
    fun saveConfig(config: UtilityConfig) {
        viewModelScope.launch {
            repository.saveConfig(config)
        }
    }

    // Customer Profiles
    fun addOrUpdateProfile(profile: CustomerProfile) {
        viewModelScope.launch {
            if (profile.id == 0L) {
                repository.insertProfile(profile)
            } else {
                repository.updateProfile(profile)
            }
        }
    }

    fun deleteProfile(profile: CustomerProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    // Calculation & Location Helpers
    fun getLatestWaterRecordForLocation(locationName: String?): WaterRecord? {
        val records = uiState.value.waterRecords
        return if (locationName.isNullOrBlank()) {
            records.maxByOrNull { it.readingDateEpochMillis }
        } else {
            records.filter { it.customerName.equals(locationName, ignoreCase = true) }
                .maxByOrNull { it.readingDateEpochMillis }
        }
    }

    fun getLatestElectricityRecordForLocation(locationName: String?): ElectricityRecord? {
        val records = uiState.value.electricityRecords
        return if (locationName.isNullOrBlank()) {
            records.maxByOrNull { it.dateEpochMillis }
        } else {
            records.filter { it.customerName.equals(locationName, ignoreCase = true) }
                .maxByOrNull { it.dateEpochMillis }
        }
    }

    fun calculateEstimatedWaterBill(usageM3: Double): Double {
        val config = uiState.value.config
        return repository.calculateWaterBill(usageM3, config)
    }

    fun estimateKwhForNominal(nominal: Double): Double {
        val state = uiState.value
        return repository.estimateKwhForNominal(nominal, state.presets, state.config)
    }

    fun syncToCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = null
            val state = uiState.value
            val result = syncManager.syncLocalToCloud(
                electricityRecords = state.electricityRecords,
                waterRecords = state.waterRecords,
                presets = state.presets,
                config = state.config,
                profiles = state.profiles
            )
            _isSyncing.value = false
            _syncMessage.value = if (result.isSuccess) {
                result.getOrNull()
            } else {
                "Sinkronisasi: ${result.exceptionOrNull()?.localizedMessage ?: "Gagal"}"
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _isSigningIn.value = true
            val webClientId = com.example.BuildConfig.GOOGLE_WEB_CLIENT_ID
            if (webClientId.isEmpty() || webClientId == "YOUR_WEB_CLIENT_ID_HERE") {
                _isSigningIn.value = false
                _syncMessage.value = "Konfigurasi Google Sign-In belum diatur. Tambahkan GOOGLE_WEB_CLIENT_ID di tab Secrets dan reload."
                return@launch
            }
            val result = googleAuthHelper.signInWithGoogle(webClientId)
            _isSigningIn.value = false
            if (result.isSuccess) {
                _currentUserEmail.value = syncManager.currentUser?.email
                _syncMessage.value = "Login berhasil."
                syncToCloud()
            } else {
                _syncMessage.value = "Login gagal: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    private val backupRestoreHelper = BackupRestoreHelper(repository)

    fun exportBackup(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            backupRestoreHelper.exportData(context, uri)
        }
    }

    fun importBackup(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            backupRestoreHelper.importData(context, uri)
        }
    }

    fun dismissSyncMessage() {
        _syncMessage.value = null
    }

    companion object {
        private val idLocale = Locale("id", "ID")
        private val currencySymbols = DecimalFormatSymbols(idLocale).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        fun formatRupiah(amount: Double): String {
            val formatter = DecimalFormat("#,##0", currencySymbols)
            return "Rp " + formatter.format(amount)
        }

        fun formatKwh(kwh: Double): String {
            val formatter = DecimalFormat("#,##0.0#", currencySymbols)
            return formatter.format(kwh) + " kWh"
        }

        fun formatM3(m3: Double): String {
            val formatter = DecimalFormat("#,##0.0#", currencySymbols)
            return formatter.format(m3) + " m³"
        }

        fun formatDate(millis: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", idLocale)
            return sdf.format(Date(millis))
        }

        fun formatDateShort(millis: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy", idLocale)
            return sdf.format(Date(millis))
        }

        fun getMonthName(month: Int): String {
            val cal = Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
            }
            return SimpleDateFormat("MMMM", idLocale).format(cal.time)
        }

        fun formatTokenFormatted(token: String): String {
            val clean = token.replace(Regex("[^0-9]"), "")
            return clean.chunked(4).joinToString("-")
        }
    }
}

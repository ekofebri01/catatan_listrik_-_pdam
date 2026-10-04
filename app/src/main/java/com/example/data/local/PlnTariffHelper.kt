package com.example.data.local

data class PlnTariffOption(
    val code: String,
    val name: String,
    val powerLabel: String,
    val ratePerKwh: Double,
    val description: String
)

object PlnTariffHelper {
    val TARIFF_OPTIONS = listOf(
        PlnTariffOption("R-1/450 VA", "R-1 / 450 VA (Subsidi)", "450 VA", 415.0, "Rumah Tangga Subsidi"),
        PlnTariffOption("R-1/900 VA", "R-1 / 900 VA (Subsidi)", "900 VA", 605.0, "Rumah Tangga Subsidi"),
        PlnTariffOption("R-1M/900 VA", "R-1M / 900 VA (Non-Subsidi)", "900 VA", 1352.0, "Rumah Tangga Mampu"),
        PlnTariffOption("R-1/1300 VA", "R-1 / 1300 VA", "1300 VA", 1444.70, "Rumah Tangga Regular"),
        PlnTariffOption("R-1/2200 VA", "R-1 / 2200 VA", "2200 VA", 1444.70, "Rumah Tangga Sedang"),
        PlnTariffOption("R-2/3500 VA", "R-2 / 3500 - 5500 VA", "3500-5500 VA", 1699.53, "Rumah Menengah Atas"),
        PlnTariffOption("R-3/6600 VA", "R-3 / 6600 VA+", "6600 VA+", 1699.53, "Rumah Besar"),
        PlnTariffOption("B-1/450-5500 VA", "B-1 / 450 - 5500 VA (Bisnis)", "Bisnis", 1444.70, "Kios / Toko / Usaha"),
        PlnTariffOption("CUSTOM", "Kustom / Tarif Lainnya", "Lainnya", 0.0, "Tarif kustom per kWh")
    )

    fun getRateForTariff(tariffCode: String, fallbackRate: Double = 1444.70): Double {
        val found = TARIFF_OPTIONS.firstOrNull { 
            it.code.equals(tariffCode, ignoreCase = true) || 
            it.name.equals(tariffCode, ignoreCase = true) ||
            tariffCode.contains(it.powerLabel, ignoreCase = true)
        }
        if (found != null && found.ratePerKwh > 0) return found.ratePerKwh
        return fallbackRate
    }

    fun getOptionByCode(tariffCode: String): PlnTariffOption {
        return TARIFF_OPTIONS.firstOrNull { 
            it.code.equals(tariffCode, ignoreCase = true) || 
            it.name.equals(tariffCode, ignoreCase = true) ||
            tariffCode.contains(it.powerLabel, ignoreCase = true)
        } ?: TARIFF_OPTIONS[3] // Default to R-1/1300 VA
    }

    fun calculateKwh(
        nominal: Double,
        ratePerKwh: Double,
        adminFee: Double = 2500.0,
        ppjTaxPercent: Double = 3.0
    ): Double {
        val netNominal = (nominal - adminFee) * (1.0 - (ppjTaxPercent / 100.0))
        if (netNominal <= 0 || ratePerKwh <= 0) return 0.0
        val kwh = netNominal / ratePerKwh
        return Math.round(kwh * 100.0) / 100.0
    }
}

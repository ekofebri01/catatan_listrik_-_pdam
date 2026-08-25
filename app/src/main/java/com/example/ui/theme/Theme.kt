package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricGoldPrimary,
    onPrimary = Color(0xFF1E1B00),
    primaryContainer = ElectricGoldSubtle,
    onPrimaryContainer = ElectricGoldSecondary,
    secondary = WaterCyanSecondary,
    onSecondary = Color(0xFF002244),
    secondaryContainer = WaterCyanSubtle,
    onSecondaryContainer = WaterCyanLight,
    tertiary = BentoVioletPrimary,
    onTertiary = Color(0xFF24005A),
    background = BentoDarkBg,
    onBackground = TextPrimaryDark,
    surface = BentoCardBg,
    onSurface = TextPrimaryDark,
    surfaceVariant = BentoTileInner,
    onSurfaceVariant = TextSecondaryDark,
    outline = BentoBorder,
    outlineVariant = BentoBorderSubtle,
    error = RoseAlert
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = WaterCyanPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = BentoVioletDark,
    onTertiary = Color.White,
    background = SlateLightBackground,
    onBackground = TextPrimaryLight,
    surface = SlateLightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = SlateLightTileInner,
    onSurfaceVariant = TextSecondaryLight,
    outline = SlateLightBorder,
    outlineVariant = Color(0xFFE2E8F0),
    error = RoseAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

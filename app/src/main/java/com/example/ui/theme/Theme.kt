package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = SurfaceLight,
    primaryContainer = EmeraldMedium,
    onPrimaryContainer = SurfaceLight,
    secondary = MagentaPink,
    onSecondary = SurfaceLight,
    secondaryContainer = MagentaContainer,
    onSecondaryContainer = MagentaDark,
    tertiary = InfoBlue,
    onTertiary = SurfaceLight,
    background = BackgroundLight,
    onBackground = SlateDark,
    surface = SurfaceLight,
    onSurface = SlateDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = SlateMedium,
    outline = CardBorder,
    error = DangerRed,
    errorContainer = DangerRedBg,
    onError = SurfaceLight
)

private val DarkColorScheme = lightColorScheme(
    primary = EmeraldMedium,
    onPrimary = SurfaceLight,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = SurfaceLight,
    secondary = MagentaPink,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    onBackground = SlateDark,
    surface = SurfaceLight,
    onSurface = SlateDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = EmeraldDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

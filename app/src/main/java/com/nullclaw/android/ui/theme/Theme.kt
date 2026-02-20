package com.nullclaw.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// NullClaw brand colors
private val NullClawPrimary = Color(0xFF6366F1) // Indigo
private val NullClawOnPrimary = Color(0xFFFFFFFF)
private val NullClawPrimaryContainer = Color(0xFFE0E7FF)
private val NullClawOnPrimaryContainer = Color(0xFF1E1B4B)

private val NullClawSecondary = Color(0xFF10B981) // Emerald
private val NullClawOnSecondary = Color(0xFFFFFFFF)
private val NullClawSecondaryContainer = Color(0xFFD1FAE5)
private val NullClawOnSecondaryContainer = Color(0xFF064E3B)

private val NullClawTertiary = Color(0xFF8B5CF6) // Violet
private val NullClawBackground = Color(0xFFFAFAFA)
private val NullClawOnBackground = Color(0xFF1F2937)
private val NullClawSurface = Color(0xFFFFFFFF)
private val NullClawOnSurface = Color(0xFF1F2937)
private val NullClawSurfaceVariant = Color(0xFFF3F4F6)
private val NullClawOnSurfaceVariant = Color(0xFF6B7280)

// Dark theme colors
private val NullClawDarkPrimary = Color(0xFF818CF8)
private val NullClawDarkOnPrimary = Color(0xFF1E1B4B)
private val NullClawDarkPrimaryContainer = Color(0xFF3730A3)
private val NullClawDarkOnPrimaryContainer = Color(0xFFE0E7FF)

private val NullClawDarkSecondary = Color(0xFF34D399)
private val NullClawDarkOnSecondary = Color(0xFF064E3B)
private val NullClawDarkSecondaryContainer = Color(0xFF065F46)
private val NullClawDarkOnSecondaryContainer = Color(0xFFD1FAE5)

private val NullClawDarkBackground = Color(0xFF111827)
private val NullClawDarkOnBackground = Color(0xFFF9FAFB)
private val NullClawDarkSurface = Color(0xFF1F2937)
private val NullClawDarkOnSurface = Color(0xFFF9FAFB)
private val NullClawDarkSurfaceVariant = Color(0xFF374151)
private val NullClawDarkOnSurfaceVariant = Color(0xFFD1D5DB)

private val DarkColorScheme = darkColorScheme(
    primary = NullClawDarkPrimary,
    onPrimary = NullClawDarkOnPrimary,
    primaryContainer = NullClawDarkPrimaryContainer,
    onPrimaryContainer = NullClawDarkOnPrimaryContainer,
    secondary = NullClawDarkSecondary,
    onSecondary = NullClawDarkOnSecondary,
    secondaryContainer = NullClawDarkSecondaryContainer,
    onSecondaryContainer = NullClawDarkOnSecondaryContainer,
    tertiary = NullClawTertiary,
    background = NullClawDarkBackground,
    onBackground = NullClawDarkOnBackground,
    surface = NullClawDarkSurface,
    onSurface = NullClawDarkOnSurface,
    surfaceVariant = NullClawDarkSurfaceVariant,
    onSurfaceVariant = NullClawDarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = NullClawPrimary,
    onPrimary = NullClawOnPrimary,
    primaryContainer = NullClawPrimaryContainer,
    onPrimaryContainer = NullClawOnPrimaryContainer,
    secondary = NullClawSecondary,
    onSecondary = NullClawOnSecondary,
    secondaryContainer = NullClawSecondaryContainer,
    onSecondaryContainer = NullClawOnSecondaryContainer,
    tertiary = NullClawTertiary,
    background = NullClawBackground,
    onBackground = NullClawOnBackground,
    surface = NullClawSurface,
    onSurface = NullClawOnSurface,
    surfaceVariant = NullClawSurfaceVariant,
    onSurfaceVariant = NullClawOnSurfaceVariant
)

@Composable
fun NullClawTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
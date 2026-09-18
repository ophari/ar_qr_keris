package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF261803),
    primaryContainer = Color(0xFF533E0E),
    onPrimaryContainer = Color(0xFFFFDF9E),
    secondary = BronzeSecondary,
    onSecondary = Color(0xFF2E1500),
    secondaryContainer = Color(0xFF582F0E),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = CrimsonAccent,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF805600),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF9E),
    onPrimaryContainer = Color(0xFF261803),
    secondary = BronzeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC2),
    onSecondaryContainer = Color(0xFF2E1500),
    tertiary = CrimsonAccent,
    onTertiary = Color.White,
    background = LightCanvas,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to rich museum dark theme for AR & 3D presentation
    dynamicColor: Boolean = false, // Keep heritage palette intentional
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

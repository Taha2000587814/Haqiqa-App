package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TechBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF00497E),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFC2C7CE), // Light secondary slate text/icons in dark mode
    onSecondary = Color(0xFF2C3137),
    tertiary = Color(0xFFFFB85F),
    background = Color(0xFF0B0E11), // Real Midnight background
    surface = Color(0xFF15191C), // Deep card background
    surfaceVariant = Color(0xFF1F2428), // Input containers
    onSurfaceVariant = Color(0xFF8E9196), // Muted label text
    onSecondaryContainer = Color(0xFFE2E2E6),
    onTertiaryContainer = Color(0xFFE2E2E6),
    onBackground = Color(0xFFE2E2E6), // Real light text
    onSurface = Color(0xFFE2E2E6), // Real light text
    outline = Color(0xFF2C3236) // Dark border
)

private val LightColorScheme = lightColorScheme(
    primary = TechBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Slate300,
    onSecondary = Color.White,
    tertiary = Amber500,
    background = Slate900, // 0xFFFDFBFF
    surface = Slate800, // 0xFFF3F3FA
    surfaceVariant = Slate700, // 0xFFEEF1F6
    onSurfaceVariant = Slate600, // 0xFF71787E
    onSecondaryContainer = Slate100,
    onTertiaryContainer = Slate100,
    onBackground = Slate100,
    onSurface = Slate100,
    outline = BorderColor
)


@Composable
fun HaqiqaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamic color optional so our custom theme colors shine on older & newer devices
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep a backward compatible alias in case there are test expectations
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    HaqiqaTheme(darkTheme = darkTheme, content = content)
}

package com.polstat.sisesapplication.ui.theme

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

// Updated Dark Color Scheme with camelCase naming
private val darkColorScheme = darkColorScheme(
    primary = Color(0xFF8BC34A),   // Blue Grey 80
    secondary = Color(0xFF00695C), // Teal 80
    tertiary = Color(0xFFFF8F00),  // Amber 80

    background = Color(0xFF121212)
)

private val lightColorScheme = lightColorScheme(
    primary = Color(0xFF31473A),    // Dark green
    secondary = Color(0xFF9EBBAB),  // Light green
    tertiary = Color(0xFFEBCBC2),   // Light pink

    // Other default colors to override
    background = Color(0xFFF0F4F8), // Light grey for the background
    surface = Color(0xFFF0F4F8),    // Same as background for consistency

    onPrimary = Color(0xFFFFFFFF),  // White for high contrast on primary
    onSecondary = Color(0xFF000000),// Black for contrast on secondary
    onTertiary = Color(0xFF000000), // Black for contrast on tertiary
    onBackground = Color(0xFF000000),// Black for contrast on background
    onSurface = Color(0xFF000000),  // Black for contrast on surface
)



@Composable
fun SiSesApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
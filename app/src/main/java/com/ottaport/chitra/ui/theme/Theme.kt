package com.ottaport.chitra.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Clean, professional white-only studio theme
private val LightColorScheme = lightColorScheme(
    primary = StudioAccent,
    onPrimary = StudioWhite,
    primaryContainer = StudioAccentContainer,
    onPrimaryContainer = StudioAccent,
    secondary = StudioTextSecondary,
    onSecondary = StudioWhite,
    secondaryContainer = StudioSurfaceVariant,
    onSecondaryContainer = StudioTextPrimary,
    tertiary = StudioKeyframeDiamond,
    onTertiary = StudioWhite,
    background = StudioWhite,
    onBackground = StudioTextPrimary,
    surface = StudioWhite,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioBorder,
    outlineVariant = StudioBorderFocused
)

@Composable
fun CHITRATheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = StudioWhite.toArgb()
            window.navigationBarColor = StudioWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

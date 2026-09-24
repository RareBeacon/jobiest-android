package com.jobiest.android.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = JobiestInk,
    onPrimary = JobiestBg,
    primaryContainer = JobiestBrand,
    onPrimaryContainer = JobiestInk,
    secondary = JobiestCobalt,
    onSecondary = JobiestBg,
    background = JobiestBg,
    onBackground = JobiestInk,
    surface = JobiestCard,
    onSurface = JobiestInk,
    surfaceVariant = JobiestBgSecondary,
    onSurfaceVariant = JobiestMuted,
    outline = JobiestBorder,
    outlineVariant = JobiestBorderSubtle,
    error = JobiestDanger,
    onError = JobiestBg
)

@Composable
fun JobiestTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = JobiestBg.toArgb()
            window.navigationBarColor = JobiestBgSecondary.toArgb()
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = true
            insets.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

package it.lucamichetti.mechlabacademy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Light = lightColorScheme(
    primary = Color(0xFF0B3A67),
    secondary = Color(0xFF5B6770),
    tertiary = Color(0xFFE57C00),
    background = Color(0xFFF7F8FA),
    surface = Color.White,
)
private val Dark = darkColorScheme(
    primary = Color(0xFF8BC5FF),
    secondary = Color(0xFFB8C4CE),
    tertiary = Color(0xFFFFB35C),
    background = Color(0xFF101418),
    surface = Color(0xFF182028),
)

@Composable
fun MechLabTheme(
    mode: String = "SYSTEM",
    textScale: Float = 1f,
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        WindowCompat.getInsetsController((view.context as Activity).window, view).isAppearanceLightStatusBars = !dark
    }
    val base = Typography()
    val typography = base.copy(
        bodyLarge = base.bodyLarge.copy(fontSize = base.bodyLarge.fontSize * textScale),
        bodyMedium = base.bodyMedium.copy(fontSize = base.bodyMedium.fontSize * textScale),
    )
    MaterialTheme(colorScheme = if (dark) Dark else Light, typography = typography, content = content)
}

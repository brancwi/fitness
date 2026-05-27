package com.muscu.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.Black,
    secondary = OrangeDark,
    surface = Surface,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = OrangeDark,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = OrangeLight,
    error = ErrorRed
)

@Composable
fun MuscuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

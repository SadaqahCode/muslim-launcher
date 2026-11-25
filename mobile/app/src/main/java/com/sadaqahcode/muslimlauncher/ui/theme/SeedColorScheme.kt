package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.material3.rememberDynamicColorScheme

@Composable
internal fun rememberColorSchemeFromSeed(
    seedArgb: Int,
    dark: Boolean,
): ColorScheme {
    return rememberDynamicColorScheme(
        seedColor = Color(seedArgb),
        isDark = dark,
    )
}
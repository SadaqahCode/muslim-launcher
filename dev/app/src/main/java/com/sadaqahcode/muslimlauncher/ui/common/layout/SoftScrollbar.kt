package com.sadaqahcode.muslimlauncher.ui.common.layout

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun rememberSoftScrollbarSettings(): ScrollbarSettings {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    return remember(outlineVariant) {
        ScrollbarSettings.Default.copy(
            thumbThickness = 4.dp,
            thumbUnselectedColor = outlineVariant.copy(alpha = 0.6f),
            thumbSelectedColor = outlineVariant.copy(alpha = 0.9f),
            scrollbarPadding = 4.dp,
        )
    }
}
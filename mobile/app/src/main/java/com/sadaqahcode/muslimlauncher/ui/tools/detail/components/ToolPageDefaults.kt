package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Content padding shared by every tool page.
 *
 * Bottom gap is deliberately smaller in landscape. On a phone held sideways
 * the vertical axis is the scarce one — a flat 48dp bottom eats 14% of a
 * ~350dp viewport for nothing. Portrait keeps the generous 48dp.
 */
internal object ToolPageDefaults {
    @Composable
    fun contentPadding(inner: PaddingValues): PaddingValues {
        val isLandscape = isWindowLandscape()
        return PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = inner.calculateTopPadding() + 8.dp,
            bottom = inner.calculateBottomPadding() + if (isLandscape) 12.dp else 48.dp,
        )
    }
}
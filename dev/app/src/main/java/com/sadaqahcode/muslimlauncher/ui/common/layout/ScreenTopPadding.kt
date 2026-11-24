package com.sadaqahcode.muslimlauncher.ui.common.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object HomeTopPadding {
 
    private val Off = 48.dp

    // On-mode values per short-side bucket.
    private val SmallPhone = 16.dp
    private val RegularPhone = 28.dp
    private val TabletMedium = 40.dp
    private val TabletLarge = 56.dp
    private val Landscape = 8.dp

    fun resolve(screen: ScreenLayout, enabled: Boolean): Dp {
        if (!enabled) return Off

        return when {
            screen.isLandscape -> Landscape
            screen.shortSide < 400f -> SmallPhone
            screen.shortSide < 600f -> RegularPhone
            screen.shortSide < 840f -> TabletMedium
            else -> TabletLarge
        }
    }
}
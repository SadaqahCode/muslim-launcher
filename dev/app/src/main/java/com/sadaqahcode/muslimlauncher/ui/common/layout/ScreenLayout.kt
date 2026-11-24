package com.sadaqahcode.muslimlauncher.ui.common.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Page max-width breakpoints ─────────────────────────────────────────────
val PageMaxWidthScreen = 840.dp
val PageMaxWidthMediumScreen = 1200.dp
val PageMaxWidthLargeScreen = 1600.dp
val ThreeColumnMinWidth = 520.dp

fun pageMaxWidthFor(widthDp: Int): Dp = when {
    widthDp >= 1920 -> PageMaxWidthLargeScreen
    widthDp >= 1200 -> PageMaxWidthMediumScreen
    else -> PageMaxWidthScreen
}

// ── Screen layout snapshot ─────────────────────────────────────────────────

@Immutable
data class ScreenLayout(
    val widthDp: Int,
    val heightDp: Int,
    val maxWidth: Dp,
    val contentMaxWidth: Dp,
    val isLandscape: Boolean,
    val isLargeLandscape: Boolean,
    val shortSide: Float,
    val transparentFraction: Float,
    val topTransparent: Dp,
) {
    companion object {
        fun from(widthDp: Int, heightDp: Int): ScreenLayout {
            val isLandscape = widthDp > heightDp
            val shortSide = minOf(widthDp, heightDp).toFloat()
            val maxWidth = pageMaxWidthFor(widthDp)
            // 0 at 600dp short side, 0.15 at 800dp+, linear between.
            val transparentFraction = ((shortSide - 600f) / 200f).coerceIn(0f, 1f) * 0.15f
            return ScreenLayout(
                widthDp = widthDp,
                heightDp = heightDp,
                maxWidth = maxWidth,
                contentMaxWidth = maxWidth,
                isLandscape = isLandscape,
                isLargeLandscape = isLandscape && widthDp >= 1200,
                shortSide = shortSide,
                transparentFraction = transparentFraction,
                topTransparent = heightDp.dp * transparentFraction,
            )
        }
    }
}

@Composable
fun rememberScreenLayout(): ScreenLayout {
    // LocalConfiguration is used deliberately — LocalWindowInfo does not update
    // on API 28 rotation.
    val config = LocalConfiguration.current
    return ScreenLayout.from(config.screenWidthDp, config.screenHeightDp)
}
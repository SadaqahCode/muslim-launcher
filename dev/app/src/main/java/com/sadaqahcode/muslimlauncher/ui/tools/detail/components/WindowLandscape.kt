package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

/**
 * True when the window is wider than it is tall.
 *
 * Uses [LocalWindowInfo.containerSize] rather than
 * `LocalConfiguration.screenWidthDp / screenHeightDp` because the Configuration
 * values:
 *  - shift when targetSdkVersion changes (insets accounting is target-gated), and
 *  - are rounded to whole Dp before you see them.
 * containerSize is the exact pixel size of the container; we convert to Dp so
 * a high-density narrow phone does not falsely read as "wide".
 */
@Composable
internal fun isWindowLandscape(): Boolean {
    val size = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val widthDp = with(density) { size.width.toDp() }
    val heightDp = with(density) { size.height.toDp() }
    return widthDp > heightDp
}
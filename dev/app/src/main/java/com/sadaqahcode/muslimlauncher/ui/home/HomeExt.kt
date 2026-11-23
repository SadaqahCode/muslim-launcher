package com.sadaqahcode.muslimlauncher.ui.home

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val PageMaxWidthScreen = 840.dp
val PageMaxWidthMediumScreen = 1200.dp
val PageMaxWidthLargeScreen = 1600.dp
val ThreeColumnMinWidth = 520.dp

fun pageMaxWidthFor(widthDp: Int): Dp =
    if (widthDp >= 1920) PageMaxWidthLargeScreen else PageMaxWidthMediumScreen
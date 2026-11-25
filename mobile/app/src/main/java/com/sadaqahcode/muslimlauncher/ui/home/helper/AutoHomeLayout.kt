package com.sadaqahcode.muslimlauncher.ui.home.helper

import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings

data class AutoHomeLayout(
    val homeColumns: Int,
    val drawerColumns: Int,
    val maxPinned: Int,
) {
    companion object {
        private val MinHomeCellWidth = 88.dp
        private val MinDrawerCellWidth = 72.dp
        private val HomeCellHeight = 88.dp
        private val HomeCellGap = 20.dp
        private val DrawerCellGap = 8.dp
        private val PortraitReservedHeight = 380.dp
        private val LandscapeReservedHeight = 60.dp

        // Same cap for both, otherwise they can never match.
        // 6 cap = Pixel Tablet 6 x 2 = 12, not 7 x 2 = 14
        private val HomeColumnsRange = 3..6
        private val DrawerColumnsRange = 3..6

        // Must match the drawer UI cap, otherwise math is for a width
        // the drawer never gets.
        private val PageMaxWidthScreen = 800.dp

        private const val MIN_MAX_PINNED = 4
        private const val MAX_MAX_PINNED = 12
        private const val MIN_PHONE_COLUMNS = 4

        private fun comfortRows(shortestDp: Int): Int =
            if (shortestDp < 600) 2 else 3

        fun resolve(
            settings: UserSettings,
            widthDp: Int,
            heightDp: Int,
        ): AutoHomeLayout {
            val isLandscape = widthDp > heightDp
            val shortSide = minOf(widthDp, heightDp)
            val isPhone = shortSide < 600
            val layoutScale = 1f + (settings.textSize.scale - 1f) * 0.5f

            // Home: same as before, from short side so rotate doesn't jump.
            val minCols = if (isPhone) MIN_PHONE_COLUMNS else 3
            val homeCols = ((shortSide.dp + HomeCellGap) / (MinHomeCellWidth + HomeCellGap))
                .toInt()
                .coerceIn(minCols, HomeColumnsRange.last)

            // Drawer: from ACTUAL drawer width, not screen width.
            // Portrait: full width. Landscape: capped to PageMaxWidthScreen.
            val drawerWidthDp = if (isLandscape) {
                minOf(widthDp, PageMaxWidthScreen.value.toInt())
            } else {
                widthDp
            }
            val drawerCols = ((drawerWidthDp.dp + DrawerCellGap) / (MinDrawerCellWidth + DrawerCellGap))
                .toInt()
                .coerceIn(DrawerColumnsRange)

            // Rows / maxPinned unchanged
            val screenHeightDp = if (isLandscape) shortSide else heightDp
            val reserved = (if (isLandscape) LandscapeReservedHeight else PortraitReservedHeight) * layoutScale
            val cellHeight = HomeCellHeight * layoutScale
            val usableHeight = (screenHeightDp.dp - reserved).coerceAtLeast(cellHeight)
            val physicalRows = (usableHeight / cellHeight).toInt().coerceAtLeast(1)

            val physicalCapacity = physicalRows * homeCols
            val comfortCapacity = comfortRows(shortSide) * homeCols
            val maxPinned = minOf(physicalCapacity, comfortCapacity)
                .coerceAtMost(MAX_MAX_PINNED)
                .coerceAtLeast(minOf(MIN_MAX_PINNED, physicalCapacity))

            return AutoHomeLayout(
                homeColumns = homeCols,
                drawerColumns = drawerCols,
                maxPinned = maxPinned,
            )
        }
    }
}
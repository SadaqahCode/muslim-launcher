package com.sadaqahcode.muslimlauncher.ui.drawer.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.sadaqahcode.muslimlauncher.ui.drawer.DrawerSheetState

internal class SheetNestedScroll(
    private val sheet: DrawerSheetState,
    private val canScrollBackward: () -> Boolean,
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source == NestedScrollSource.UserInput &&
            available.y < 0f &&
            sheet.progress < 1f
        ) {
            val used = sheet.dragBy(-available.y)
            return Offset(0f, -used)
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        if (source == NestedScrollSource.UserInput && available.y > 0f) {
            val used = sheet.dragBy(-available.y)
            return Offset(0f, -used)
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (sheet.progress < 1f) {
            sheet.settle(available.y)
            return available
        }
        if (available.y > 0f && !canScrollBackward()) {
            sheet.settle(available.y)
            return available
        }
        return Velocity.Zero
    }
}
package com.sadaqahcode.muslimlauncher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

/**
 * Reusable modal bottom sheet with the app's standard appearance.
 *
 * @param onDismiss        Called when the user dismisses the sheet (scrim tap,
 *   back press, or swipe-down when [showDragHandle] is true).
 * @param heightFraction   Fraction of available height the sheet's content should
 *   occupy. `null` (default) = wrap content — the sheet only grows as tall as its
 *   content. Set to `0.92f` for a near-fullscreen sheet (e.g. the dhikr reader).
 * @param showDragHandle   When `true`, renders the standard rounded pill at the
 *   top-center — the standard affordance that signals "swipe down to dismiss".
 *   When `false` (default), the sheet is a flat rectangle with no handle; dismissal
 *   is via scrim tap, back press, or an explicit close button in the content.
 * @param containerColor   Sheet surface color. Defaults to the standard 96% opaque
 *   background — matches the app's glass / sheet aesthetic.
 * @param contentColor     Text/icon color on the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrimBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    heightFraction: Float? = null,
    showDragHandle: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.96f),
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = RectangleShape,
        tonalElevation = 0.dp,
        dragHandle = if (showDragHandle) {
            { BottomSheetDefaults.DragHandle() }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (heightFraction != null) Modifier.fillMaxHeight(heightFraction)
                    else Modifier
                ),
            content = content,
        )
    }
}
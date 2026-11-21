package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.ui.components.ScrimBottomSheet
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSheet(
    dhikrs: List<Dhikr>,
    startIndex: Int,
    selectable: Boolean,
    currentKey: String,
    favorites: Set<String>,
    language: String,
    onUse: (Dhikr) -> Unit,
    onToggleFavorite: (Dhikr) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val pager = rememberPagerState(
        initialPage = startIndex.coerceIn(0, dhikrs.lastIndex),
    ) { dhikrs.size }
    val page = pager.currentPage.coerceIn(0, dhikrs.lastIndex)
    val shown = dhikrs[page]
    val isCurrent = shown.freeKey() == currentKey
    val isFavorite = shown.freeKey() in favorites

    // Only swallow fling, let drag pass to sheet
    val swallowFlingOnly = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                return available
            }
        }
    }

    ScrimBottomSheet(
        onDismiss = onDismiss,
        sheetState = sheetState,
        heightFraction = 1f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${page + 1} / ${dhikrs.size}",
                style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (selectable) {
                FavoriteButton(
                    favorite = isFavorite,
                    language = language,
                    scheme = colors,
                    onClick = { onToggleFavorite(shown) },
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = tr(language, "Tutup", "Close"),
                )
            }
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .nestedScroll(swallowFlingOnly)
        ) {
            HorizontalPager(
                state = pager,
                beyondViewportPageCount = 1,
                modifier = Modifier.fillMaxSize(),
            ) { index ->
                ReaderPage(dhikrs[index], language)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PillButton(
                label = if (isCurrent) {
                    tr(language, "Sedang dipakai", "In use")
                } else {
                    tr(language, "Pakai dzikir ini", "Use this dhikr")
                },
                filled = !isCurrent,
                enabled = selectable && !isCurrent,
                onClick = {
                    onUse(shown)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
            )
            PillButton(
                label = tr(language, "Tutup", "Close"),
                filled = false,
                enabled = true,
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
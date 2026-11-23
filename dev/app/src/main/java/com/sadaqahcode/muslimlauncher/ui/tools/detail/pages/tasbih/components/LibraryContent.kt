package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.domain.model.tools.text
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.guidedKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun LibraryContent(
    state: TasbihUiState,
    language: String,
    onPickFree: (String) -> Unit,
    onPickGuided: (Int) -> Unit,
    onToggleFavFree: (String) -> Unit,
    onToggleFavGuided: (String) -> Unit,
    modifier: Modifier,
) {
    val defaultColors = MaterialTheme.colorScheme
    var tab by remember { mutableIntStateOf(if (state.autoRun) 1 else 0) }
    val favLabel = tr(language, "Sematkan / lepas favorit", "Pin / unpin favorite")

    Surface(
        color = defaultColors.background,
        contentColor = defaultColors.onBackground,
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = tr(language, "Dzikir", "Dhikr"),
                style = MaterialTheme.typography.titleLarge,
                color = defaultColors.onSurface,
            )

            SegmentedPicker(
                labels = listOf(tr(language, "Bebas", "Free"), tr(language, "Terpandu", "Guided")),
                selectedIndex = tab,
                onSelect = { tab = it },
                scheme = defaultColors,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = if (tab == 0) {
                    tr(
                        language,
                        "Pilih dzikir apa saja dan hitung sebebasnya, tanpa urutan.",
                        "Pick any dhikr and count freely, in no particular order.",
                    )
                } else {
                    tr(
                        language,
                        "Menjalani semua langkah dan berpindah sendiri.",
                        "Runs through every step and moves on by itself.",
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                color = defaultColors.onSurfaceVariant,
            )

            if (tab == 0) {
                val currentKey = state.currentFree?.freeKey()
                val (pinned, others) = state.freeList.partition { it.freeKey() in state.favoriteFree }

                @Composable
                fun FreeRow(d: Dhikr) {
                    val key = d.freeKey()
                    LibraryRow(
                        title = d.displayTitle(language),
                        subtitle = d.text(language).takeUnless { it.isNullOrEmpty() } ?: d.arabic,
                        badge = null,
                        selected = !state.autoRun && key == currentKey,
                        favorite = key in state.favoriteFree,
                        favLabel = favLabel,
                        onClick = { onPickFree(key) },
                        onToggleFavorite = { onToggleFavFree(key) },
                        scheme = defaultColors
                    )
                }

                if (pinned.isNotEmpty()) {
                    SectionLabel(tr(language, "Disematkan", "Pinned"), scheme = defaultColors)
                    pinned.forEach { FreeRow(it) }
                    SectionLabel(tr(language, "Semua dzikir", "All dhikr"), scheme = defaultColors)
                }
                others.forEach { FreeRow(it) }
            } else {
                val guided = state.guidedDisplay
                val (pinned, others) = guided.partition { it.guidedKey() in state.favoriteGuided }

                @Composable
                fun GuidedRow(item: DhikrItem) {
                    // guidedDisplay is sorted, onPickGuided needs index in state.items
                    val originalIndex = state.items.indexOfFirst { it.id == item.id }
                    LibraryRow(
                        title = item.name(language),
                        subtitle = item.entries.joinToString(" · ") {
                            "${it.dhikr.displayTitle(language)} ×${it.repetitions}"
                        },
                        badge = "${item.entries.sumOf { it.repetitions }}×",
                        selected = state.autoRun && originalIndex == state.selectedItemIndex,
                        favorite = item.guidedKey() in state.favoriteGuided,
                        favLabel = favLabel,
                        onClick = { if (originalIndex >= 0) onPickGuided(originalIndex) },
                        onToggleFavorite = { onToggleFavGuided(item.guidedKey()) },
                        scheme = defaultColors
                    )
                }


                if (pinned.isNotEmpty()) {
                    SectionLabel(tr(language, "Disematkan", "Pinned"), scheme = defaultColors)
                    pinned.forEach { GuidedRow(it) }
                    SectionLabel(tr(language, "Semua rangkaian", "All routines"), scheme = defaultColors)
                }

                others.forEach { GuidedRow(it) }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

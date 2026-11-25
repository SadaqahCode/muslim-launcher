package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.domain.model.tools.text
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.guidedKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr
import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun LibraryContent(
    state: TasbihUiState,
    language: String,
    onPickFree: (String) -> Unit,
    onPickGuided: (Int) -> Unit,
    onToggleFavFree: (String) -> Unit,
    onToggleFavGuided: (String) -> Unit,
    modifier: Modifier = Modifier,
    onLibraryPickerBounds: (Rect?) -> Unit = {},
) {
    val softScrollbar = rememberSoftScrollbarSettings()
    val defaultColors = MaterialTheme.colorScheme
    var tab by remember { mutableIntStateOf(if (state.autoRun) 1 else 0) }
    val favLabel = tr(language, "Sematkan / lepas favorit", "Pin / unpin favorite")
    val hPad = Modifier.padding(horizontal = 20.dp)
    val listState = rememberLazyListState()

    Surface(
        color = defaultColors.background,
        contentColor = defaultColors.onBackground,
        modifier = modifier,
    ) {
        LazyColumnScrollbar(
            state = listState,
            settings = softScrollbar,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "title") {
                    Box(hPad) {
                        Text(
                            text = tr(language, "Dzikir", "Dhikr"),
                            style = MaterialTheme.typography.titleLarge,
                            color = defaultColors.onSurface,
                        )
                    }
                }

                stickyHeader(key = "picker") {
                    Box(Modifier.fillMaxWidth().background(defaultColors.background)) {
                        Box(hPad) {
                            SegmentedPicker(
                                labels = listOf(
                                    tr(language, "Bebas", "Free"),
                                    tr(language, "Terpandu", "Guided"),
                                ),
                                selectedIndex = tab,
                                onSelect = { tab = it },
                                scheme = defaultColors,
                                modifier = Modifier.fillMaxWidth()
                                    .onGloballyPositioned { onLibraryPickerBounds(it.boundsInRoot()) },
                            )
                        }
                    }
                }

                item(key = "desc") {
                    Box(hPad) {
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
                    }
                }

                if (tab == 0) {
                    val currentKey = state.currentFree?.freeKey()
                    val (pinned, others) = state.freeList.partition { it.freeKey() in state.favoriteFree }

                    if (pinned.isNotEmpty()) {
                        item(key = "free-label-pinned") {
                            Box(hPad) {
                                SectionLabel(tr(language, "Disematkan", "Pinned"), scheme = defaultColors)
                            }
                        }
                        items(pinned, key = { "free-p-${it.freeKey()}" }) { d ->
                            Box(hPad) {
                                FreeRow(
                                    d = d,
                                    language = language,
                                    currentKey = currentKey,
                                    favoriteFree = state.favoriteFree,
                                    autoRun = state.autoRun,
                                    favLabel = favLabel,
                                    onPickFree = onPickFree,
                                    onToggleFavFree = onToggleFavFree,
                                    scheme = defaultColors,
                                )
                            }
                        }
                        item(key = "free-label-all") {
                            Box(hPad) {
                                SectionLabel(tr(language, "Semua dzikir", "All dhikr"), scheme = defaultColors)
                            }
                        }
                    }
                    items(others, key = { "free-${it.freeKey()}" }) { d ->
                        Box(hPad) {
                            FreeRow(
                                d = d,
                                language = language,
                                currentKey = currentKey,
                                favoriteFree = state.favoriteFree,
                                autoRun = state.autoRun,
                                favLabel = favLabel,
                                onPickFree = onPickFree,
                                onToggleFavFree = onToggleFavFree,
                                scheme = defaultColors,
                            )
                        }
                    }
                } else {
                    val guided = state.guidedDisplay
                    val (pinned, others) = guided.partition { it.guidedKey() in state.favoriteGuided }

                    if (pinned.isNotEmpty()) {
                        item(key = "guided-label-pinned") {
                            Box(hPad) {
                                SectionLabel(tr(language, "Disematkan", "Pinned"), scheme = defaultColors)
                            }
                        }
                        items(pinned, key = { "guided-p-${it.guidedKey()}" }) { item ->
                            Box(hPad) {
                                GuidedRow(
                                    item = item,
                                    language = language,
                                    state = state,
                                    favoriteGuided = state.favoriteGuided,
                                    favLabel = favLabel,
                                    onPickGuided = onPickGuided,
                                    onToggleFavGuided = onToggleFavGuided,
                                    scheme = defaultColors,
                                )
                            }
                        }
                        item(key = "guided-label-all") {
                            Box(hPad) {
                                SectionLabel(tr(language, "Semua rangkaian", "All routines"), scheme = defaultColors)
                            }
                        }
                    }
                    items(others, key = { "guided-${it.guidedKey()}" }) { item ->
                        Box(hPad) {
                            GuidedRow(
                                item = item,
                                language = language,
                                state = state,
                                favoriteGuided = state.favoriteGuided,
                                favLabel = favLabel,
                                onPickGuided = onPickGuided,
                                onToggleFavGuided = onToggleFavGuided,
                                scheme = defaultColors,
                            )
                        }
                    }
                }

                item(key = "tail") { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun FreeRow(
    d: Dhikr,
    language: String,
    currentKey: String?,
    favoriteFree: Set<String>,
    autoRun: Boolean,
    favLabel: String,
    onPickFree: (String) -> Unit,
    onToggleFavFree: (String) -> Unit,
    scheme: androidx.compose.material3.ColorScheme,
) {
    val key = d.freeKey()
    LibraryRow(
        title = d.displayTitle(language),
        subtitle = d.text(language).takeUnless { it.isNullOrEmpty() } ?: d.arabic,
        badge = null,
        selected = !autoRun && key == currentKey,
        favorite = key in favoriteFree,
        favLabel = favLabel,
        onClick = { onPickFree(key) },
        onToggleFavorite = { onToggleFavFree(key) },
        scheme = scheme,
    )
}

@Composable
private fun GuidedRow(
    item: DhikrItem,
    language: String,
    state: TasbihUiState,
    favoriteGuided: Set<String>,
    favLabel: String,
    onPickGuided: (Int) -> Unit,
    onToggleFavGuided: (String) -> Unit,
    scheme: androidx.compose.material3.ColorScheme,
) {
    val originalIndex = state.items.indexOfFirst { it.id == item.id }
    LibraryRow(
        title = item.name(language),
        subtitle = item.entries.joinToString(" · ") {
            "${it.dhikr.displayTitle(language)} ×${it.repetitions}"
        },
        badge = "${item.entries.sumOf { it.repetitions }}×",
        selected = state.autoRun && originalIndex == state.selectedItemIndex,
        favorite = item.guidedKey() in favoriteGuided,
        favLabel = favLabel,
        onClick = { if (originalIndex >= 0) onPickGuided(originalIndex) },
        onToggleFavorite = { onToggleFavGuided(item.guidedKey()) },
        scheme = scheme,
    )
}
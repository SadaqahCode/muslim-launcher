package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.title
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components.LibraryRow
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.SalawatUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr
import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun SalawatListContent(
    state: SalawatUiState,
    language: String,
    onPick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    modifier: Modifier = Modifier,
    lcColors: ColorScheme,
    defaultColors: ColorScheme,
) {
    val scrollbar = rememberSoftScrollbarSettings()
    val listState = rememberLazyListState()
    val selectedId = state.selected?.id
    val display = state.displayList

    Surface(color = defaultColors.background, contentColor = defaultColors.onBackground) {
        Column(modifier) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = tr(language, "Shalawat", "Salawat"),
                    style = MaterialTheme.typography.titleLarge,
                    color = defaultColors.onSurface,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onToggleFavoritesOnly) {
                    Text(
                        text = if (state.showingFavoritesOnly) {
                            tr(language, "Semua", "All")
                        } else {
                            tr(language, "Favorit", "Favorites")
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = defaultColors.onSurface,
                    )
                }
            }

            if (display.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state.showingFavoritesOnly) {
                            tr(language, "Tidak ada favorit", "No favorites yet")
                        } else {
                            tr(language, "Tidak ada shalawat", "No salawat available")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = defaultColors.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumnScrollbar(
                    state = listState,
                    settings = scrollbar,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(display, key = { it.id }) { item ->
                            val favorite = item.id in state.favorites
                            LibraryRow(
                                title = item.title(language),
                                subtitle = item.reference.orEmpty(),
                                badge = null,
                                selected = item.id == selectedId,
                                favorite = favorite,
                                favLabel = if (favorite) {
                                    tr(language, "Hapus dari favorit", "Remove from favorites")
                                } else {
                                    tr(language, "Tambah ke favorit", "Add to favorites")
                                },
                                onClick = { onPick(item.id) },
                                onToggleFavorite = { onToggleFavorite(item.id) },
                                scheme = defaultColors,
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.context
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.text
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.title
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.PageHeader
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat.SalawatUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr
import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun SalawatDetailPane(
    toolTitle: String,
    state: SalawatUiState,
    language: String,
    showSidebar: Boolean,
    onToggleFavorite: (Int) -> Unit,
    onRefresh: () -> Unit,
    onOpenList: (() -> Unit)?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    lcColors: ColorScheme,
    defaultColors: ColorScheme,
    split: Boolean,
) {
    val scrollbar = rememberSoftScrollbarSettings()
    val listState = rememberLazyListState()
    val selected = state.selected

    Column(modifier) {
        SalawatHeader(
            language = language,
            toolTitle = toolTitle,
            showSidebar = showSidebar,
            selectedTitle = selected?.title(language),
            favorite = selected?.id?.let { it in state.favorites } ?: false,
            refreshing = state.isRefreshing,
            showListIcon = onOpenList != null,
            onOpenList = onOpenList,
            onToggleFavorite = { selected?.id?.let(onToggleFavorite) },
            onRefresh = onRefresh,
            onClose = onClose,
            lcColors = lcColors,
            defaultColors = defaultColors,
        )

        if (selected == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tr(language, "Pilih shalawat", "Choose a salawat"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = lcColors.onSurfaceVariant,
                )
            }
            return@Column
        }

        val arabic = selected.arabic.takeIf { it.isNotBlank() }
        val translation = selected.text(language)?.takeIf { it.isNotBlank() }
        val latin = selected.latin?.takeIf { it.isNotBlank() }
        val context = selected.context(language)?.takeIf { it.isNotBlank() }
        val reference = selected.reference?.takeIf { it.isNotBlank() }

        LazyColumnScrollbar(
            state = listState,
            settings = scrollbar,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (arabic != null || translation != null) {
                    item(key = "content-${selected.id}") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glass()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (arabic != null) {
                                Text(
                                    text = arabic,
                                    style = ArabicType.headlineSmall,
                                    color = defaultColors.onSurface,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            if (translation != null) {
                                Text(
                                    text = translation,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = defaultColors.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }

                if (latin != null) {
                    item(key = "latin-${selected.id}") {
                        Text(
                            text = latin,
                            style = MaterialTheme.typography.bodyMedium,
                            color = lcColors.onSurface,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (context != null || reference != null) {
                    item(key = "context-${selected.id}") {
                        val body = buildAnnotatedString {
                            if (context != null) append(context)
                            if (reference != null) {
                                if (context != null) append("\n")
                                withStyle(
                                    SpanStyle(
                                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                        fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                                        letterSpacing = MaterialTheme.typography.labelMedium.letterSpacing,
                                    )
                                ) {
                                    append("— $reference")
                                }
                            }
                        }
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = lcColors.onSurface,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SalawatHeader(
    language: String,
    toolTitle: String,
    selectedTitle: String?,
    favorite: Boolean,
    refreshing: Boolean,
    showSidebar: Boolean,
    showListIcon: Boolean,
    onOpenList: (() -> Unit)?,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
    lcColors: ColorScheme,
    defaultColors: ColorScheme,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // left
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PageHeader(
                language = language,
                onClose = onClose,
                showSidebar = showSidebar,
                scheme = lcColors,
            ) {
                val openListOnTap = showListIcon && onOpenList != null

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .then(
                            if (openListOnTap) {
                                Modifier.clickable(
                                    onClickLabel = tr(language, "Buka daftar", "Open list"),
                                    onClick = { onOpenList.invoke() },
                                )
                            } else {
                                Modifier
                            }
                        ),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = toolTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = lcColors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                        )
                        if (showListIcon) {
                            Text(
                                    text = tr(language, "Ganti", "Change"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = lcColors.primary,
                                    modifier = Modifier.padding(start = 6.dp),
                                )
                        }
                    }
                    Text(
                        text = selectedTitle.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = lcColors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        // right
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onToggleFavorite,
                enabled = selectedTitle != null,
            ) {
                Icon(
                    imageVector = if (favorite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (favorite) lcColors.primary
                    else lcColors.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = onRefresh,
                enabled = !refreshing,
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = lcColors.onSurfaceVariant,
                )
            }

            if (showListIcon && onOpenList != null) {
                IconButton(onClick = onOpenList) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                        contentDescription = null,
                        tint = lcColors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.guidedKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun MainPane(
    toolTitle: String,
    state: TasbihUiState,
    item: DhikrItem?,
    dhikr: Dhikr,
    language: String,
    split: Boolean,
    showPicker: Boolean,
    actions: Actions,
    onOpenLibrary: () -> Unit,
    onOpenReader: () -> Unit,
    onClose: () -> Unit,
    onOpenHapticDialog: () -> Unit,
    colors: ColorScheme,
    modifier: Modifier,
) {
    val guided = state.autoRun && item != null
    val finished = guided && state.finished

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PageHeader(
            language,
            onClose,
            scheme = colors
        ) {
            val pickerModifier = if (showPicker) {
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        role = Role.Button,
                        onClickLabel = tr(language, "Pilih dzikir", "Choose dhikr"),
                        onClick = onOpenLibrary,
                    )
            } else {
                Modifier
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(pickerModifier)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = toolTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                    )
                    if (showPicker) {
                        if(guided){
                            Text(
                                text = tr(language, "Ganti", "Change"),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.primary,
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }else{
                            Text(
                                text = "•  " + tr(language, "Ganti dzikir", "Change dhikr"),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.primary,
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (guided) item.name(language) else dhikr.displayTitle(language),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (showPicker) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
            if (guided) {
                GuidedBadge(language, actions.onExitGuided)
            }
            FavoriteButton(
                favorite = state.isFavoriteCurrent,
                language = language,
                scheme = colors,
                onClick = {
                    if (guided) item.let { actions.onToggleFavGuided(it.guidedKey()) }
                    else actions.onToggleFavFree(dhikr.freeKey())
                },
            )
            IconButton(onClick = onOpenHapticDialog) {
                Icon(
                    imageVector = Icons.Outlined.Vibration,
                    contentDescription = tr(language, "Umpan balik sentuhan", "Touch feedback"),
                    tint = colors.onSurfaceVariant,
                )
            }
        }

        if (guided) {
            GuidedProgress(
                progress = state.progress,
                stepLabel = "${tr(language, "Langkah", "Step")} ${state.entryIndex + 1}/${item.entries.size}",
                totalLabel = "${state.doneRepetitions}/${state.totalRepetitions}",
                scheme = colors,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        if (finished) {
            FinishPanel(
                item = item,
                language = language,
                onRepeat = actions.onRestartGuided,
                onDone = actions.onExitGuided,
                scheme = colors,
                modifier = Modifier.weight(1f),
            )
            return@Column
        }

        val freeList = state.freeDisplay
        val freeIndex = freeList.indexOfFirst { it.freeKey() == dhikr.freeKey() }

        val words: @Composable (Modifier) -> Unit = { m ->
            WordsPane(
                dhikr = dhikr,
                language = language,
                scheme = colors,
                swipeEnabled = !state.autoRun && freeList.size > 1,
                onPrevious = { actions.onMoveFree(-1) },
                onNext = { actions.onMoveFree(1) },
                onExpand = onOpenReader,
                guidedItem = if (guided) item else null,
                guidedEntryIndex = state.entryIndex,
                position = if (!state.autoRun && freeIndex >= 0 && freeList.size > 1) {
                    "${freeIndex + 1} / ${freeList.size}"
                } else {
                    null
                },
                modifier = m,
            )
        }
        val counter: @Composable (Modifier) -> Unit = { m ->
            TapCounter(
                state.count,
                state.target,
                language,
                actions.onTap,
                scheme = colors,
                m)
        }
        val dock: @Composable () -> Unit = {
            ControlDock(
                state = state,
                language = language,
                actions = actions,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
            )
        }

        if (split) {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                words(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    counter(Modifier.weight(1f))
                    dock()
                }
            }
        } else {
            words(Modifier.weight(0.8f))
            counter(Modifier.weight(1.2f))
            dock()
        }
    }
}

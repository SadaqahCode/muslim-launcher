package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.name
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

@Composable
fun FinishPanel(
    item: DhikrItem,
    language: String,
    onRepeat: () -> Unit,
    onDone: () -> Unit,
    scheme: ColorScheme,
    modifier: Modifier,
) {
    val defaultColors = MaterialTheme.colorScheme
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val appear by animateFloatAsState(if (shown) 1f else 0f, tween(400), label = "finishIn")

    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .graphicsLayer {
                    alpha = appear
                    translationY = (1f - appear) * 24.dp.toPx()
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .glass(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = defaultColors.onSurface,
                    modifier = Modifier.size(36.dp),
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = tr(language, "Alhamdulillah, selesai", "Alhamdulillah, completed"),
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "${item.name(language)} · ${item.entries.sumOf { it.repetitions }}×",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glass(RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item.entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = defaultColors.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = entry.dhikr.displayTitle(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = defaultColors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "×${entry.repetitions}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                            color = defaultColors.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PillButton(
                    label = tr(language, "Ulangi", "Repeat"),
                    filled = false,
                    onClick = onRepeat,
                    modifier = Modifier.weight(1f),
                )
                PillButton(
                    label = tr(language, "Selesai", "Done"),
                    filled = true,
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

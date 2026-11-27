package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.DhikrItem
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.swipeNavigate
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


/**
- Word preview. Long text is truncated, then "View complete" appears; the entire area can be tapped
- to open the full reader.
 *
- Free     : swipe left/right to change dhikr (favorites first).
- Guided   : cannot be swiped (accidental swipes must not skip steps),
-            dots at the bottom are step indicators only.
 */
@Composable
fun WordsPane(
    dhikr: Dhikr,
    language: String,
    scheme: ColorScheme,
    swipeEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
    guidedItem: DhikrItem?,
    guidedEntryIndex: Int,
    position: String?,
    modifier: Modifier,
) {
    var truncated by remember(dhikr) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeNavigate(enabled = swipeEnabled, onPrevious = onPrevious, onNext = onNext)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                role = Role.Button,
                onClickLabel = tr(language, "Lihat lengkap", "View in full"),
                onClick = onExpand,
            )
            .padding(horizontal = 28.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Crossfade(targetState = dhikr, label = "words") { d ->
                WordsText(d, language, onOverflow = { if (it) truncated = true }, scheme = scheme)
            }

            if (truncated) {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .glass(CircleShape)
                        .padding(start = 14.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = tr(language, "Lihat lengkap", "View in full"),
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onPrimaryContainer,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = scheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            if (guidedItem != null && guidedItem.entries.size > 1) {
                EntryDots(
                    count = guidedItem.entries.size,
                    selected = guidedEntryIndex,
                    labelFor = { i -> guidedItem.entries[i].dhikr.displayTitle(language) },
                    scheme = scheme
                )
            } else if (position != null) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}
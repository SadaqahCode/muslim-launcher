package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.home.PrayerHeroState
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.theme.withHalo

enum class PrayerRowState { Started, Current, Upcoming }

@Composable
internal fun HeroSection(
    state: PrayerHeroState,
    textScale: AppTextSize,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
) {
    val lc = LocalLegibleColors.current
    val c = lc.scheme
    val defaultColors = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // left
            Column {
                Text(
                    state.hijriDate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textScale.scaled(MaterialTheme.typography.labelLarge.withHalo(lc.halo)),
                    color = c.onBackground,
                )
                Text(
                    state.gregorianDate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textScale.scaled(MaterialTheme.typography.bodySmall.withHalo(lc.halo)),
                    color = c.onBackground.copy(alpha = 0.72f),
                )
            }
            // right - only if not null
            if (onOpenSettings != null) {
                Box(
                    modifier = Modifier
                        .glass(RoundedCornerShape(20.dp))
                        .size(38.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(38.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Theme settings",
                            tint = defaultColors.onBackground.copy(alpha = 0.65f),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Next prayer",
            style = textScale.scaled(MaterialTheme.typography.bodySmall.withHalo(lc.halo)),
            color = c.onBackground.copy(alpha = 0.72f),
        )
        Text(
            state.nextPrayerName,
            style = textScale.scaled(MaterialTheme.typography.displayMedium
                .copy(fontWeight = FontWeight(350))
                .withHalo(lc.halo)),
            color = c.onBackground,
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                state.nextPrayerTime,
                style = textScale.scaled(MaterialTheme.typography.headlineSmall
                    .copy(fontFeatureSettings = "tnum")
                    .withHalo(lc.halo)),
                color = c.onBackground,
            )
            Text(
                state.timeRemaining,
                style = textScale.scaled(MaterialTheme.typography.titleMedium
                    .copy(fontFeatureSettings = "tnum")
                    .withHalo(lc.halo)),
                color = c.primary,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            state.windowLine,
            style = textScale.scaled(MaterialTheme.typography.bodySmall.withHalo(lc.halo)),
            color = c.onBackground.copy(alpha = 0.72f),
        )

        Spacer(Modifier.height(24.dp))

        val currentIdx = state.prayerTimes.indexOfFirst { it.first == state.currentWindow }
        val startedBefore = if (currentIdx >= 0) currentIdx
        else state.prayerTimes.indexOfFirst { it.first == state.nextPrayerName }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glass(RoundedCornerShape(20.dp)),
        ) {
            Row(modifier = Modifier.padding(6.dp)) {
                state.prayerTimes.forEachIndexed { index, (name, time) ->
                    val rowState = when {
                        index == currentIdx -> PrayerRowState.Current
                        index < startedBefore -> PrayerRowState.Started
                        else -> PrayerRowState.Upcoming
                    }
                    PrayerTimeItem(name, time, rowState, textScale, Modifier.weight(1f))
                }
            }
        }
    }
}
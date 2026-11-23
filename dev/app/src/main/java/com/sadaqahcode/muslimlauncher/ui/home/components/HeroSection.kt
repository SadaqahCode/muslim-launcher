package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.home.PrayerHeroState
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.theme.withHalo

@Composable
internal fun HeroSection(
    state: PrayerHeroState,
    textScale: AppTextSize,
    modifier: Modifier = Modifier,
) {
    val lc = LocalLegibleColors.current
    val c = lc.scheme

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            state.hijriDate,
            style = textScale.scaled(MaterialTheme.typography.labelLarge.withHalo(lc.halo)),
            color = c.onBackground,
        )
        Text(
            state.gregorianDate,
            style = textScale.scaled(MaterialTheme.typography.bodySmall.withHalo(lc.halo)),
            color = c.onBackground.copy(alpha = 0.72f),
        )

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

internal enum class PrayerRowState { Started, Current, Upcoming }

@Composable
private fun PrayerTimeItem(
    name: String,
    time: String,
    rowState: PrayerRowState,
    textScale: AppTextSize,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val color = when (rowState) {
        PrayerRowState.Started -> colors.onBackground.copy(alpha = 0.65f)
        PrayerRowState.Current -> colors.onBackground
        PrayerRowState.Upcoming -> colors.onSurface
    }
    val weight = if (rowState == PrayerRowState.Current) FontWeight.SemiBold
    else FontWeight.Normal

    val spoken = when (rowState) {
        PrayerRowState.Started -> "Passed"
        PrayerRowState.Current -> "Current window"
        PrayerRowState.Upcoming -> "Upcoming"
    }
    val base = modifier.semantics(mergeDescendants = true) { stateDescription = spoken }
    val itemModifier = if (rowState == PrayerRowState.Current) {
        base.glass(RoundedCornerShape(14.dp), GlassEmphasis.Selected)
    } else {
        base
    }

    Column(
        modifier = itemModifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (rowState == PrayerRowState.Current) {
            Box(Modifier.size(4.dp).background(colors.primary, CircleShape))
            Spacer(Modifier.height(4.dp))
        } else {
            Spacer(Modifier.height(8.dp))
        }
        Text(name, style = textScale.scaled(MaterialTheme.typography.labelSmall), color = color, fontWeight = weight)
        Text(
            time,
            style = textScale.scaled(MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum")),
            color = color,
            fontWeight = weight,
        )
    }
}
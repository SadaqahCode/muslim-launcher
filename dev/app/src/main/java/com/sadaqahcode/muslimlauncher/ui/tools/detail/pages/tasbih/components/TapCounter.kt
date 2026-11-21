package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

@Composable
fun TapCounter(
    count: Int,
    target: Int,
    language: String,
    onTap: () -> Unit,
    scheme: ColorScheme,
    modifier: Modifier,
) {
    val defaultColors = MaterialTheme.colorScheme

    val hasTarget = target > 0
    val done = hasTarget && count >= target

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "counterPress",
    )
    val progress = animateFloatAsState(
        targetValue = if (hasTarget) (count.toFloat() / target).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "counterProgress",
    )
    val track = scheme.onSurface.copy(alpha = 0.10f)
    val arc = scheme.onSurface

    val label = tr(language, "Penghitung tasbih", "Tasbih counter")
    val addOne = tr(language, "Tambah satu", "Add one")
    val stateText = if (hasTarget) "$count ${tr(language, "dari", "of")} $target" else "$count"

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = addOne,
                onClick = onTap,
            )
            .semantics {
                contentDescription = label
                stateDescription = stateText
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center,
    ) {
        val size = minOf(maxWidth - 32.dp, maxHeight - 16.dp, 440.dp).coerceAtLeast(120.dp)
        val stroke = (size.value * 0.022f).coerceIn(5f, 10f).dp
        val countSize = (size.value * 0.30f).coerceIn(56f, 120f).sp

        Box(Modifier.size(size), contentAlignment = Alignment.Center) {
            Canvas(Modifier.matchParentSize()) {
                val strokePx = stroke.toPx()
                val inset = strokePx / 2f
                val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
                drawArc(
                    color = track,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx),
                )
                if (progress.value > 0f) {
                    drawArc(
                        color = arc,
                        startAngle = -90f,
                        sweepAngle = 360f * progress.value,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .size(size * 0.9f)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .glass(
                        shape = CircleShape,
                        emphasis = if (done) GlassEmphasis.Selected else GlassEmphasis.Regular,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = countSize,
                        lineHeight = countSize * 1.1f,
                        fontWeight = FontWeight.Light,
                        fontFeatureSettings = "tnum",
                    ),
                    color = defaultColors.onSurface,
                )
                if (hasTarget) {
                    Text(
                        text = "${tr(language, "dari", "of")} $target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = defaultColors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

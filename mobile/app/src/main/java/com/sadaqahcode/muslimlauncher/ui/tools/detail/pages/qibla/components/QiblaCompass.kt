package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import kotlin.math.cos
import kotlin.math.sin

data class CompassLabels(val north: String, val east: String, val south: String, val west: String)

private const val DISC_FRACTION = 0.52f

@Composable
internal fun QiblaCompass(
    dialRotation: Float,
    qiblaBearing: Float?,
    aligned: Boolean,
    labels: CompassLabels,
    centerPrimary: String,
    centerSecondary: String?,
    description: String,
    stateText: String,
    lsScheme: ColorScheme,
    defaultScheme: ColorScheme,
    modifier: Modifier = Modifier,
) {
    val rotationState = animateFloatAsState(
        targetValue = dialRotation,
        animationSpec = tween(durationMillis = 110, easing = LinearEasing),
        label = "qiblaDial",
    )
    val emphasis = animateFloatAsState(
        targetValue = if (aligned) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "qiblaAligned",
    )
    val measurer = rememberTextMeasurer()
    val letterStyle = MaterialTheme.typography.labelLarge

    BoxWithConstraints(
        modifier = modifier.semantics {
            contentDescription = description
            stateDescription = stateText
        },
        contentAlignment = Alignment.Center,
    ) {
        val bigSize = (maxWidth.value * 0.15f).coerceIn(28f, 64f).sp

        // Glass disc in the center, where the angle numbers go.
        Box(
            Modifier
                .fillMaxSize(DISC_FRACTION)
                .glass(CircleShape, if (aligned) GlassEmphasis.Selected else GlassEmphasis.Regular),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = centerPrimary,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = bigSize,
                        lineHeight = bigSize * 1.1f,
                        fontWeight = FontWeight.Light,
                        fontFeatureSettings = "tnum",
                    ),
                    color = defaultScheme.onSurface,
                )
                if (centerSecondary != null) {
                    Text(
                        text = centerSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        color = defaultScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Canvas(Modifier.fillMaxSize()) {
            val rotation = rotationState.value
            val e = emphasis.value
            val cx = size.width / 2f
            val cy = size.height / 2f
            // Leave room outside the ring for the Ka'bah marker and the needle.
            val r = size.minDimension / 2f - 28.dp.toPx()

            // 0° = top, clockwise.
            fun polar(radius: Float, deg: Float): Offset {
                val rad = Math.toRadians(deg.toDouble())
                return Offset(cx + radius * sin(rad).toFloat(), cy - radius * cos(rad).toFloat())
            }

            // Ring: thicker and brighter when aligned with the Qibla.
            drawCircle(
                color = lsScheme.onSurface.copy(alpha = 0.18f + 0.62f * e),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = (1.5f + 1.5f * e).dp.toPx()),
            )

            // Scale ticks every 5°.
            for (i in 0 until 72) {
                val base = i * 5f
                val major = i % 18 == 0
                val mid = i % 6 == 0
                val length = (if (major) 14.dp else if (mid) 10.dp else 6.dp).toPx()
                val alpha = if (major) 0.55f else if (mid) 0.35f else 0.20f
                drawLine(
                    color = lsScheme.onSurface.copy(alpha = alpha),
                    start = polar(r, base + rotation),
                    end = polar(r - length, base + rotation),
                    strokeWidth = (if (major) 2.dp else 1.dp).toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Cardinal letters: rotate in position but stay upright and readable.
            val cardinals = listOf(
                0f to labels.north,
                90f to labels.east,
                180f to labels.south,
                270f to labels.west,
            )
            for ((deg, text) in cardinals) {
                val layout = measurer.measure(text, letterStyle)
                val c = polar(r - 34.dp.toPx(), deg + rotation)
                drawText(
                    textLayoutResult = layout,
                    color = if (deg == 0f) lsScheme.onSurface else lsScheme.onSurfaceVariant,
                    topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f),
                )
            }

            // Ka'bah: soft line from the disc edge plus a marker on the ring.
            if (qiblaBearing != null) {
                val angle = qiblaBearing + rotation
                drawLine(
                    color = lsScheme.onSurface.copy(alpha = 0.62f + 0.38f * e),
                    start = polar(size.minDimension * DISC_FRACTION / 2f + 4.dp.toPx(), angle),
                    end = polar(r - 18.dp.toPx(), angle),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawKaaba(center = polar(r, angle), side = 24.dp.toPx(), scheme = lsScheme)
            }

            val needleBase = cy - r - 26.dp.toPx()
            val needle = Path().apply {
                moveTo(cx - 9.dp.toPx(), needleBase)
                lineTo(cx + 9.dp.toPx(), needleBase)
                lineTo(cx, needleBase + 12.dp.toPx())
                close()
            }
            drawPath(needle, color = lsScheme.onSurface.copy(alpha = 0.6f + 0.4f * e))
        }
    }
}

private fun DrawScope.drawKaaba(center: Offset, side: Float, scheme: ColorScheme) {
    val surface = scheme.surface.copy(alpha = 1f)
    val onSurface = scheme.onSurface.copy(alpha = 1f)
    val surfaceIsDarker = surface.luminance() < onSurface.luminance()
    val body = if (surfaceIsDarker) surface else onSurface
    val band = if (surfaceIsDarker) onSurface else surface

    val topLeft = Offset(center.x - side / 2f, center.y - side / 2f)
    val box = Size(side, side)
    val corner = CornerRadius(3.dp.toPx())

    drawRoundRect(color = body, topLeft = topLeft, size = box, cornerRadius = corner)

    // Kiswah band across the upper third.
    drawRect(
        color = band,
        topLeft = Offset(topLeft.x, topLeft.y + side * 0.26f),
        size = Size(side, 3.dp.toPx()),
    )

    // Outline separates the body from the background.
    drawRoundRect(
        color = onSurface,
        topLeft = topLeft,
        size = box,
        cornerRadius = corner,
        style = Stroke(width = 1.5.dp.toPx()),
    )
}
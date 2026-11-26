package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

/** Huruf arah mata angin sesuai bahasa. */
data class CompassLabels(val north: String, val east: String, val south: String, val west: String)

/**
 * Kompas Qibla. Piringan berputar mengikuti arah perangkat (utara selalu menunjuk utara),
 * penanda Ka'bah tetap di sudut kiblatnya pada piringan, dan jarum di atas tetap diam.
 * Saat penanda Ka'bah tepat di bawah jarum, perangkat menghadap kiblat.
 *
 * Tanpa warna aksen, tanpa gerak berulang selain putaran piringan itu sendiri.
 *
 * @param dialRotation sudut kumulatif piringan dalam derajat (lihat QiblaUiState.dialRotation).
 */
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
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
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
    val onSurface = colors.onSurface
    val onSurfaceVariant = colors.onSurfaceVariant
    val background = colors.background

    BoxWithConstraints(
        modifier = modifier.semantics {
            contentDescription = description
            stateDescription = stateText
        },
        contentAlignment = Alignment.Center,
    ) {
        val bigSize = (maxWidth.value * 0.15f).coerceIn(28f, 64f).sp

        // Cakram kaca di tengah, tempat angka sudut.
        Box(
            Modifier
                .fillMaxSize(0.58f)
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
                    color = onSurface,
                )
                if (centerSecondary != null) {
                    Text(
                        text = centerSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        color = onSurfaceVariant,
                    )
                }
            }
        }

        Canvas(Modifier.fillMaxSize()) {
            val rotation = rotationState.value
            val e = emphasis.value
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.minDimension / 2f - 16.dp.toPx()

            // 0° = atas, searah jarum jam.
            fun polar(radius: Float, deg: Float): Offset {
                val rad = Math.toRadians(deg.toDouble())
                return Offset(cx + radius * sin(rad).toFloat(), cy - radius * cos(rad).toFloat())
            }

            // Cincin: lebih tebal dan jelas saat sejajar.
            drawCircle(
                color = onSurface.copy(alpha = 0.18f + 0.62f * e),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = (1.5f + 1.5f * e).dp.toPx()),
            )

            // Garis skala tiap 5°.
            for (i in 0 until 72) {
                val base = i * 5f
                val major = i % 18 == 0
                val mid = i % 6 == 0
                val length = (if (major) 14.dp else if (mid) 10.dp else 6.dp).toPx()
                val alpha = if (major) 0.55f else if (mid) 0.35f else 0.20f
                drawLine(
                    color = onSurface.copy(alpha = alpha),
                    start = polar(r, base + rotation),
                    end = polar(r - length, base + rotation),
                    strokeWidth = (if (major) 2.dp else 1.dp).toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Huruf mata angin: ikut berputar posisinya, tetap tegak terbaca.
            val cardinals = listOf(0f to labels.north, 90f to labels.east, 180f to labels.south, 270f to labels.west)
            for ((deg, text) in cardinals) {
                val layout = measurer.measure(text, letterStyle)
                val c = polar(r - 34.dp.toPx(), deg + rotation)
                drawText(
                    textLayoutResult = layout,
                    color = if (deg == 0f) onSurface else onSurfaceVariant,
                    topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f),
                )
            }

            // Ka'bah: garis lembut dari tengah dan penanda kotak di cincin.
            if (qiblaBearing != null) {
                val angle = qiblaBearing + rotation
                drawLine(
                    color = onSurface.copy(alpha = 0.22f + 0.2f * e),
                    start = polar(r * 0.62f, angle),
                    end = polar(r - 18.dp.toPx(), angle),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                val m = polar(r, angle)
                val side = 24.dp.toPx()
                val topLeft = Offset(m.x - side / 2f, m.y - side / 2f)
                drawRoundRect(
                    color = onSurface,
                    topLeft = topLeft,
                    size = Size(side, side),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
                // Pita tipis seperti kiswah.
                drawRect(
                    color = background,
                    topLeft = Offset(topLeft.x, topLeft.y + side * 0.30f),
                    size = Size(side, 2.5.dp.toPx()),
                )
            }

            // Jarum tetap di atas.
            val needle = Path().apply {
                moveTo(cx - 9.dp.toPx(), cy - r - 14.dp.toPx())
                lineTo(cx + 9.dp.toPx(), cy - r - 14.dp.toPx())
                lineTo(cx, cy - r - 2.dp.toPx())
                close()
            }
            drawPath(needle, color = onSurface.copy(alpha = 0.6f + 0.4f * e))
        }
    }
}
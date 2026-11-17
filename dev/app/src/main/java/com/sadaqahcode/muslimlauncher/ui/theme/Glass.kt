package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

enum class GlassEmphasis { Regular, Selected }


@Composable
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(24.dp),
    emphasis: GlassEmphasis = GlassEmphasis.Regular,
): Modifier {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val selected = emphasis == GlassEmphasis.Selected

    // surfaceContainer is set in Theme.kt. surfaceContainerHigh is not, so it would fall
    // back to the Material default (a purple-grey) and stain the green palette.
    val fill = when {
        selected -> if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f)
        isDark -> colors.surfaceContainer.copy(alpha = 0.62f)
        else -> colors.surface.copy(alpha = 0.78f)
    }

    val layers = remember(isDark, selected) { GlassLayers.create(isDark, selected) }
    val depth = layers.depth

    return this
        .clip(shape)
        .background(fill)
        .background(layers.sheen)
        .then(if (depth != null) Modifier.background(depth) else Modifier)
        .border(width = 1.dp, brush = layers.rim, shape = shape)
}

private class GlassLayers(
    val sheen: Brush,
    val depth: Brush?,
    val rim: Brush,
) {
    companion object {
        fun create(isDark: Boolean, selected: Boolean): GlassLayers {
            val sheenAlpha = when {
                selected -> if (isDark) 0.16f else 0.70f
                else -> if (isDark) 0.10f else 0.55f
            }
            val sheen = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = sheenAlpha),
                    Color.White.copy(alpha = 0f),
                ),
                start = Offset.Zero,
                end = Offset.Infinite,
            )

            // A selected pill is already lifted; it needs no extra shade.
            val depth = if (selected) null else Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = if (isDark) 0.12f else 0.04f),
                )
            )

            val rimColors = when {
                isDark && selected -> listOf(
                    Color.White.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.14f),
                )
                isDark -> listOf(
                    Color.White.copy(alpha = 0.32f),
                    Color.White.copy(alpha = 0.05f),
                    Color.White.copy(alpha = 0.12f),
                )
                selected -> listOf(
                    Color.White,
                    Color.White.copy(alpha = 0.50f),
                    Color.Black.copy(alpha = 0.06f),
                )
                else -> listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color.White.copy(alpha = 0.30f),
                    Color.Black.copy(alpha = 0.07f),
                )
            }
            val rim = Brush.linearGradient(
                colors = rimColors,
                start = Offset.Zero,
                end = Offset.Infinite,
            )
            return GlassLayers(sheen, depth, rim)
        }
    }
}
package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun EntryDots(
    count: Int,
    selected: Int,
    labelFor: (Int) -> String,
    scheme: ColorScheme,
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            val isSelected = i == selected
            val width by animateDpAsState(if (isSelected) 22.dp else 8.dp, tween(150), label = "dot")
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 16.dp, minHeight = 24.dp)
                    .semantics { contentDescription = labelFor(i) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .width(width)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) scheme.onSurface else scheme.onSurface.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

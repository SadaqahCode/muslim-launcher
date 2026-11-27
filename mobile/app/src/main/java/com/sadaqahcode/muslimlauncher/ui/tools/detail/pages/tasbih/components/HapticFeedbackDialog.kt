package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.HapticMode
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun HapticFeedbackDialog(
    current: HapticMode,
    language: String,
    onSelect: (HapticMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    ScrimDialog(onDismiss = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = tr(language, "Umpan balik sentuhan", "Touch feedback"),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                )
                Text(
                    text = tr(
                        language,
                        "Pilih bagaimana perangkat merespons setiap ketukan.",
                        "Choose how your device responds to each tap.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                FeedbackRow(
                    title = tr(language, "Haptik", "Haptic"),
                    subtitle = tr(
                        language,
                        "Getaran halus bawaan sistem. Menghormati pengaturan sentuhan Anda.",
                        "Subtle system-tuned vibration. Respects your touch-feedback settings.",
                    ),
                    selected = current == HapticMode.Haptic,
                    onClick = { onSelect(HapticMode.Haptic) },
                )
                FeedbackRow(
                    title = tr(language, "Getaran", "Vibration"),
                    subtitle = tr(
                        language,
                        "Getaran langsung. Tetap terasa meski haptik sistem dimatikan.",
                        "Direct motor vibration. Fires even when system haptics is off.",
                    ),
                    selected = current == HapticMode.Vibration,
                    onClick = { onSelect(HapticMode.Vibration) },
                )
                FeedbackRow(
                    title = tr(language, "Senyap", "Silent"),
                    subtitle = tr(
                        language,
                        "Tanpa umpan balik sentuhan.",
                        "No tactile feedback.",
                    ),
                    selected = current == HapticMode.Silent,
                    onClick = { onSelect(HapticMode.Silent) },
                )
            }
        }
    }
}

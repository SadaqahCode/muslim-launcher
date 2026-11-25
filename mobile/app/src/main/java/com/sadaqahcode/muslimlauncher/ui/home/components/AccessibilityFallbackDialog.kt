package com.sadaqahcode.muslimlauncher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.home.strings.rememberAccessibilityDialogStrings
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass

enum class AccessibilityDialogKind { Shade, Lock }

private enum class LockMethod { Accessibility, DeviceAdmin }

private val OptionShape = RoundedCornerShape(16.dp)

@Composable
internal fun AccessibilityFallbackDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    kind: AccessibilityDialogKind = AccessibilityDialogKind.Shade,
    onRequestDeviceAdmin: (() -> Unit)? = null,
    isAccessibilityEnabled: Boolean = false,
    isDeviceAdminEnabled: Boolean = false,
) {
    val s = rememberAccessibilityDialogStrings()
    val isLock = kind == AccessibilityDialogKind.Lock
    var selected by remember { mutableStateOf(LockMethod.Accessibility) }
    val scrollState = rememberScrollState()

    val onContinue = {
        if (isLock) {
            if (selected == LockMethod.Accessibility) onOpenSettings()
            else onRequestDeviceAdmin?.invoke()
        } else {
            onOpenSettings()
        }
        onDismiss()
    }

    ScrimDialog(
        onDismiss = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (isLock) s.titleLock else s.titleShade,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { heading() },
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isLock) s.subtitleLock else s.subtitleShade,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))

                    if (isLock) {
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            LockOptionCard(
                                title = s.accessibilityTitle,
                                subtitle = s.accessibilityDesc,
                                badge = s.accessibilityBadge,
                                icon = Icons.Default.Fingerprint,
                                selected = selected == LockMethod.Accessibility,
                                enabledLabel = if (isAccessibilityEnabled) s.enabledLabel else null,
                                onClick = { selected = LockMethod.Accessibility },
                            )
                            LockOptionCard(
                                title = s.deviceAdminTitle,
                                subtitle = s.deviceAdminDesc,
                                badge = s.deviceAdminBadge,
                                icon = Icons.Default.Lock,
                                selected = selected == LockMethod.DeviceAdmin,
                                enabledLabel = if (isDeviceAdminEnabled) s.enabledLabel else null,
                                onClick = { selected = LockMethod.DeviceAdmin },
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                            Text(s.continueLabel)
                        }
                    } else {
                        Text(
                            text = s.infoShade,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                            Text(s.openSettingsLabel)
                        }
                    }

                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text(s.notNowLabel)
                    }
                }
        }
    }
}

@Composable
private fun LockOptionCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    selected: Boolean,
    enabledLabel: String?,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    shape = OptionShape,
                    emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
                )
                .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.primary)
            Column(Modifier.weight(1f)) {
                Text(text = enabledLabel ?: badge, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
            RadioButton(selected = selected, onClick = null)
        }
}
package com.sadaqahcode.muslimlauncher.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val isLock = kind == AccessibilityDialogKind.Lock
    var selected by remember { mutableStateOf(LockMethod.Accessibility) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.88f else 0.92f)
                .heightIn(max = if (isLandscape) 440.dp else 620.dp),
        ) {
            if (isLandscape) {
                // LANDSCAPE: split - left = title + actions, right = scrollable options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (isLock) "Double-tap to lock" else "Swipe-down",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.semantics { heading() },
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (isLock) "Choose how you want to lock. You only need one."
                            else "Your device blocks the standard way to open the notification panel.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onContinue, modifier = Modifier.weight(1f)) {
                                Text(if (isLock) "Continue" else "Open Settings")
                            }
                            TextButton(onClick = onDismiss) { Text("Not now") }
                        }
                    }

                    if (isLock) {
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                                .verticalScroll(scrollState)
                                .selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            LockOptionCard(
                                title = "Accessibility",
                                subtitle = "Fingerprint still works. Can reset after restart.",
                                badge = "Recommended",
                                icon = Icons.Default.Fingerprint,
                                selected = selected == LockMethod.Accessibility,
                                enabledLabel = if (isAccessibilityEnabled) "Enabled" else null,
                                onClick = { selected = LockMethod.Accessibility },
                                compact = true,
                            )
                            LockOptionCard(
                                title = "Device Admin",
                                subtitle = "Survives restart. May ask PIN instead of fingerprint.",
                                badge = "Most stable",
                                icon = Icons.Default.Lock,
                                selected = selected == LockMethod.DeviceAdmin,
                                enabledLabel = if (isDeviceAdminEnabled) "Enabled" else null,
                                onClick = { selected = LockMethod.DeviceAdmin },
                                compact = true,
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "This is only used to open the shade. It does not read your screen or store data.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            } else {
                // PORTRAIT: vertical + scrollable
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
                        text = if (isLock) "Double-tap to lock" else "Swipe-down",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { heading() },
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isLock) "Choose how you want to lock. You only need one."
                        else "Your device blocks the standard way to open the notification panel.",
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
                                title = "Accessibility",
                                subtitle = "Fingerprint still works. Can reset after restart.",
                                badge = "Recommended",
                                icon = Icons.Default.Fingerprint,
                                selected = selected == LockMethod.Accessibility,
                                enabledLabel = if (isAccessibilityEnabled) "Enabled" else null,
                                onClick = { selected = LockMethod.Accessibility },
                                compact = false,
                            )
                            LockOptionCard(
                                title = "Device Admin",
                                subtitle = "Survives restart. May ask PIN instead of fingerprint.",
                                badge = "Most stable",
                                icon = Icons.Default.Lock,
                                selected = selected == LockMethod.DeviceAdmin,
                                enabledLabel = if (isDeviceAdminEnabled) "Enabled" else null,
                                onClick = { selected = LockMethod.DeviceAdmin },
                                compact = false,
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                            Text("Continue")
                        }
                    } else {
                        Text(
                            text = "This is only used to open the shade. It does not read your screen or store data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                            Text("Open Settings")
                        }
                    }

                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Not now")
                    }
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
    compact: Boolean,
) {
    val colors = MaterialTheme.colorScheme

    if (compact) {
        // Landscape: vertical card, fits narrow column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glass(
                    shape = OptionShape,
                    emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
                )
                .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
                .padding(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                Text(text = enabledLabel ?: badge, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                RadioButton(selected = selected, onClick = null, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    } else {
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
}
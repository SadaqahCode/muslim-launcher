package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.LocationPrecision
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.PermissionState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr

@Composable
fun PermissionStatusDialog(
    language: String,
    permission: PermissionState,
    precision: LocationPrecision,
    onRequestCoarse: () -> Unit,
    onRequestFine: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
    defaultScheme: ColorScheme,
) {
    // Derive the two independent states from your existing model — nothing new.
    val coarse: PermissionState = permission
    val fine: PermissionState = when {
        precision == LocationPrecision.Fine      -> PermissionState.Granted
        permission == PermissionState.Blocked    -> PermissionState.Blocked
        else                                     -> PermissionState.NotGranted
    }

    ScrimDialog(onDismiss = onDismiss, maxWidth = 380.dp) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = defaultScheme.surface,
            contentColor = defaultScheme.onSurface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = tr(language, "Status izin lokasi", "Location permission status"),
                    style = MaterialTheme.typography.titleLarge,
                    color = defaultScheme.onSurface,
                )
                Text(
                    text = tr(
                        language,
                        "Kiblat butuh izin lokasi. Izin perkiraan cukup untuk kebanyakan kasus; izin presisi memakai GPS agar lebih akurat, terutama di dalam ruangan.",
                        "Qibla needs location permission. Approximate is enough for most cases; precise uses GPS for better accuracy, especially indoors.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = defaultScheme.onSurfaceVariant,
                )

                PermissionRow(
                    language = language,
                    title = tr(language, "Lokasi perkiraan", "Approximate location"),
                    subtitle = coarse.describe(language, coarse = true),
                    status = coarse,
                    leadingIcon = Icons.Outlined.MyLocation,
                    onClick = coarse.action(onRequestCoarse, onOpenSettings),
                    defaultScheme = defaultScheme,
                )

                PermissionRow(
                    language = language,
                    title = tr(language, "Lokasi presisi", "Precise location"),
                    subtitle = fine.describe(language, coarse = false),
                    status = fine,
                    leadingIcon = Icons.Outlined.LocationOn,
                    onClick = fine.action(onRequestFine, onOpenSettings),
                    defaultScheme = defaultScheme,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(tr(language, "Tutup", "Close"))
                    }
                    TextButton(onClick = onOpenSettings) {
                        Text(tr(language, "Setelan", "Settings"))
                    }
                }
            }
        }
    }
}

/* ── Row ─────────────────────────────────────────────────────────── */

@Composable
private fun PermissionRow(
    language: String,
    title: String,
    subtitle: String,
    status: PermissionState,
    leadingIcon: ImageVector,
    onClick: (() -> Unit)?,
    defaultScheme: ColorScheme,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = defaultScheme.surfaceVariant,
        contentColor = defaultScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = defaultScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = defaultScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = defaultScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = status.indicator(),
                contentDescription = status.label(language),
                tint = status.indicatorTint(defaultScheme),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/* ── PermissionState → visuals / action (no new type) ────────────── */

private fun PermissionState.indicator(): ImageVector = when (this) {
    PermissionState.Granted    -> Icons.Outlined.CheckCircle
    PermissionState.NotGranted -> Icons.Outlined.Cancel
    PermissionState.Blocked    -> Icons.Outlined.Lock
}

private fun PermissionState.indicatorTint(scheme: ColorScheme) = when (this) {
    PermissionState.Granted    -> scheme.primary
    PermissionState.NotGranted -> scheme.onSurfaceVariant
    PermissionState.Blocked    -> scheme.error
}

private fun PermissionState.label(language: String): String = when (this) {
    PermissionState.Granted    -> tr(language, "Diizinkan", "Granted")
    PermissionState.NotGranted -> tr(language, "Belum diizinkan", "Not granted")
    PermissionState.Blocked    -> tr(language, "Diblokir", "Blocked")
}

private fun PermissionState.describe(language: String, coarse: Boolean): String = when (this) {
    PermissionState.Granted ->
        if (coarse) tr(language, "Diizinkan — cukup untuk kebanyakan kasus", "Granted — enough for most cases")
        else        tr(language, "Diizinkan — GPS aktif, lebih akurat",   "Granted — GPS active, more accurate")
    PermissionState.NotGranted ->
        tr(language, "Ketuk untuk meminta izin", "Tap to request permission")
    PermissionState.Blocked ->
        tr(language, "Diblokir — buka setelan", "Blocked — open settings")
}

/** Granted → row is not clickable; Blocked → settings; NotGranted → request. */
private fun PermissionState.action(
    request: () -> Unit,
    openSettings: () -> Unit,
): (() -> Unit)? = when (this) {
    PermissionState.Granted    -> null
    PermissionState.NotGranted -> request
    PermissionState.Blocked    -> openSettings
}
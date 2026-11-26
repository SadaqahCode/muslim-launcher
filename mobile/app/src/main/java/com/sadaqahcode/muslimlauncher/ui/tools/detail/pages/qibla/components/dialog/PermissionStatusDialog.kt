package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.dialog

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
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.LocationPrecision
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.LocationProblem
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.PermissionState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr

@Composable
fun PermissionStatusDialog(
    language: String,
    gpsEnabled: Boolean,
    permission: PermissionState,
    precision: LocationPrecision,
    locationProblem: LocationProblem,
    onRequestCoarse: () -> Unit,
    onRequestFine: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    defaultScheme: ColorScheme,
) {
    val fine: PermissionState = when {
        permission != PermissionState.Granted -> permission
        precision == LocationPrecision.Fine   -> PermissionState.Granted
        else                                  -> PermissionState.NotGranted
    }

    val gpsOff         = !gpsEnabled
    val locationFailed = locationProblem == LocationProblem.Failed
    val allGood        = !gpsOff && !locationFailed &&
            permission == PermissionState.Granted &&
            fine   == PermissionState.Granted

    ScrimDialog(onDismiss = onDismiss, maxWidth = 400.dp) {
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
                    text = tr(language, "Status lokasi & izin", "Location & permissions"),
                    style = MaterialTheme.typography.titleLarge,
                    color = defaultScheme.onSurface,
                )
                Text(
                    text = if (allGood) {
                        tr(
                            language,
                            "Semua sudah siap — kompas bisa dipakai. Ketuk baris mana pun untuk membuka setelan.",
                            "Everything is ready — the compass can be used. Tap any row to open settings.",
                        )
                    } else {
                        tr(
                            language,
                            "Kiblat butuh lokasi aktif dan izin yang tepat. Perbaiki item di bawah lalu coba lagi.",
                            "Qibla needs location turned on and the right permissions. Fix the items below and try again.",
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = defaultScheme.onSurfaceVariant,
                )

                /* 1. Location service — always shown, always tappable */
                if (gpsOff) {
                    StatusRow(
                        title = tr(language, "Lokasi HP mati", "Location is off"),
                        subtitle = tr(language, "Ketuk untuk menyalakan", "Tap to turn on"),
                        leadingIcon = Icons.Outlined.LocationOff,
                        leadingTint = defaultScheme.error,
                        trailingIcon = Icons.Outlined.Warning,
                        trailingTint = defaultScheme.error,
                        onClick = onOpenLocationSettings,
                        defaultScheme = defaultScheme,
                    )
                } else {
                    StatusRow(
                        title = tr(language, "Lokasi HP aktif", "Location is on"),
                        subtitle = tr(language, "Ketuk untuk membuka setelan", "Tap to open settings"),
                        leadingIcon = Icons.Outlined.LocationOn,
                        leadingTint = defaultScheme.primary,
                        trailingIcon = Icons.Outlined.CheckCircle,
                        trailingTint = defaultScheme.primary,
                        onClick = onOpenLocationSettings,
                        defaultScheme = defaultScheme,
                    )
                }

                /* 2. Approximate permission — always tappable */
                StatusRow(
                    title = tr(language, "Lokasi perkiraan", "Approximate location"),
                    subtitle = when (permission) {
                        PermissionState.Granted    ->
                            tr(language, "Diizinkan — ketuk untuk setelan", "Granted — tap for settings")
                        PermissionState.NotGranted ->
                            tr(language, "Ketuk untuk meminta izin", "Tap to request permission")
                        PermissionState.Blocked    ->
                            tr(language, "Diblokir — buka setelan", "Blocked — open settings")
                    },
                    leadingIcon = Icons.Outlined.MyLocation,
                    leadingTint = defaultScheme.primary,
                    trailingIcon = permission.indicator(),
                    trailingTint = permission.indicatorTint(defaultScheme),
                    onClick = permission.action(onRequestCoarse, onOpenAppSettings),
                    defaultScheme = defaultScheme,
                )

                /* 3. Precise permission — always tappable */
                StatusRow(
                    title = tr(language, "Lokasi presisi", "Precise location"),
                    subtitle = when {
                        fine == PermissionState.Granted ->
                            tr(language, "Diizinkan — ketuk untuk setelan", "Granted — tap for settings")
                        fine == PermissionState.Blocked ->
                            tr(language, "Diblokir — buka setelan", "Blocked — open settings")
                        permission != PermissionState.Granted ->
                            tr(language, "Butuh izin perkiraan dulu", "Needs approximate permission first")
                        else ->
                            tr(language, "Ketuk untuk meminta izin", "Tap to request permission")
                    },
                    leadingIcon = Icons.Outlined.MyLocation,
                    leadingTint = when {
                        fine == PermissionState.Granted  -> defaultScheme.primary
                        fine == PermissionState.Blocked  -> defaultScheme.error
                        permission == PermissionState.Granted -> defaultScheme.primary
                        else                             -> defaultScheme.outline
                    },
                    trailingIcon = fine.indicator(),
                    trailingTint = fine.indicatorTint(defaultScheme),
                    onClick = when {
                        // Coarse not yet granted — nothing to request; open settings anyway.
                        permission != PermissionState.Granted -> onOpenAppSettings
                        else -> fine.action(onRequestFine, onOpenAppSettings)
                    },
                    defaultScheme = defaultScheme,
                )

                /* 4. Location lookup failed — retry */
                if (locationFailed) {
                    StatusRow(
                        title = tr(language, "Lokasi gagal didapat", "Location lookup failed"),
                        subtitle = tr(language, "Ketuk untuk coba lagi", "Tap to try again"),
                        leadingIcon = Icons.Outlined.Warning,
                        leadingTint = defaultScheme.error,
                        trailingIcon = Icons.Outlined.Warning,
                        trailingTint = defaultScheme.error,
                        onClick = onRetry,
                        defaultScheme = defaultScheme,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(tr(language, "Tutup", "Close"))
                    }
                    TextButton(onClick = onOpenAppSettings) {
                        Text(tr(language, "Setelan aplikasi", "App settings"))
                    }
                }
            }
        }
    }
}

/* ── Row primitive — always clickable ───────────────────────────── */

@Composable
private fun StatusRow(
    title: String,
    subtitle: String,
    leadingIcon: ImageVector,
    leadingTint: Color,
    trailingIcon: ImageVector,
    trailingTint: Color,
    onClick: () -> Unit,
    defaultScheme: ColorScheme,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = defaultScheme.surfaceVariant,
        contentColor = defaultScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = leadingTint,
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
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingTint,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/* ── PermissionState helpers ─────────────────────────────────────── */

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

/**
 * Every state is tappable:
 *   Granted    → open app settings (inspect/revoke)
 *   NotGranted → request the permission
 *   Blocked    → open app settings
 */
private fun PermissionState.action(
    request: () -> Unit,
    openSettings: () -> Unit,
): () -> Unit = when (this) {
    PermissionState.Granted    -> openSettings
    PermissionState.NotGranted -> request
    PermissionState.Blocked    -> openSettings
}
package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.LocationOrigin
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.LocationProblem
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.PermissionState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.QiblaUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.SensorStatus
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.statusText
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@Composable
fun InfoPanel(
    state: QiblaUiState,
    language: String,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    onEdit: () -> Unit,
    onShowCalibration: () -> Unit,
    lsScheme: ColorScheme,
    defaultScheme: ColorScheme,
    modifier: Modifier,
) {
    val distance = state.distanceKm

    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = statusText(state, language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = lsScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (distance != null && distance > 0) {
            Text(
                text = "${NumberFormat.getIntegerInstance().format(distance)} km " +
                        tr(language, "dari Ka'bah", "from the Kaaba"),
                style = MaterialTheme.typography.bodyMedium,
                color = lsScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        when (state.sensor) {
            SensorStatus.NeedsCalibration -> NoticeCard(
                text = tr(
                    language,
                    "Kompas kurang akurat. Ketuk untuk cara kalibrasi.",
                    "The compass is not accurate. Tap to see how to calibrate.",
                ),
                onClick = onShowCalibration,
                scheme = defaultScheme,
            )

            SensorStatus.Unavailable -> NoticeCard(
                text = tr(
                    language,
                    "Kompas tidak terbaca. Ketuk untuk info.",
                    "No compass reading. Tap for details.",
                ),
                onClick = onShowCalibration,
                scheme = defaultScheme,
            )

            SensorStatus.Checking, SensorStatus.Ok -> Unit
        }

        LocationCard(
            state = state,
            language = language,
            onRequestPermission = onRequestPermission,
            onOpenSettings = onOpenSettings,
            onRefresh = onRefresh,
            onEdit = onEdit,
            defaultScheme = defaultScheme,
        )
    }
}

@Composable
private fun NoticeCard(
    text: String,
    onClick: () -> Unit,
    scheme: ColorScheme,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    )
}

@Composable
private fun LocationCard(
    state: QiblaUiState,
    language: String,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    onEdit: () -> Unit,
    defaultScheme: ColorScheme,
) {
    val location = state.location

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (location != null) {
            val p = location.point
            Text(
                text = location.label ?: String.format(Locale.US, "%.4f°, %.4f°", p.lat, p.lon),
                style = MaterialTheme.typography.titleSmall,
                color = defaultScheme.onSurface,
            )
            val source = when (location.origin) {
                LocationOrigin.Device -> tr(language, "Dari perangkat", "From your device")
                LocationOrigin.Manual -> tr(language, "Diisi manual", "Entered manually")
            }
            val timeFormatted = remember(location.savedAtMillis) {
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
                    .format(Date(location.savedAtMillis))
            }

            val label = remember(source, timeFormatted) {
                buildAnnotatedString {
                    append(source)
                    append(" · ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(timeFormatted)
                    }
                }
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = defaultScheme.onSurfaceVariant,
            )

        } else {
            Text(
                text = tr(language, "Lokasi dibutuhkan", "Location needed"),
                style = MaterialTheme.typography.titleSmall,
                color = defaultScheme.onSurface,
            )
            Text(
                text = when {
                    state.locationBusy -> tr(language, "Mencari lokasi…", "Finding your location…")
                    state.permission == PermissionState.Blocked -> tr(
                        language,
                        "Izin lokasi ditolak. Aktifkan lewat pengaturan aplikasi, atau isi lokasi manual.",
                        "Location permission was denied. Enable it in app settings, or enter a location manually.",
                    )
                    state.permission == PermissionState.NotGranted -> tr(
                        language,
                        "Lokasi perkiraan dipakai untuk menghitung arah dan disimpan di HP ini.",
                        "Approximate location is used to work out the direction and is saved on this phone.",
                    )
                    else -> tr(
                        language,
                        "Lokasi belum didapat. Coba lagi atau isi manual.",
                        "Could not get your location. Try again or enter it manually.",
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                color = defaultScheme.onSurfaceVariant,
            )
        }

        val problem = when (state.locationProblem) {
            LocationProblem.LocationOff ->
                tr(language, "Layanan lokasi di perangkat sedang mati.", "Location services are turned off.")

            LocationProblem.Failed ->
                tr(language, "Lokasi baru belum didapat.", "Could not get a new location.")

            LocationProblem.None -> null
        }
        if (problem != null) {
            Text(
                text = problem,
                style = MaterialTheme.typography.bodySmall,
                color = defaultScheme.onSurfaceVariant,
            )
        }

        // ── buttons row ────────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val primary = Modifier.weight(1f)
            when (state.permission) {
                PermissionState.Granted -> SmallButton(
                    label = when {
                        state.locationBusy -> tr(language, "Mencari…", "Finding…")
                        location != null -> tr(language, "Perbarui", "Refresh")
                        else -> tr(language, "Coba lagi", "Try again")
                    },
                    filled = true,
                    enabled = !state.locationBusy,
                    onClick = onRefresh,
                    scheme = defaultScheme,
                    modifier = primary,
                )

                PermissionState.NotGranted -> SmallButton(
                    label = tr(language, "Izinkan lokasi", "Allow location"),
                    filled = true,
                    onClick = onRequestPermission,
                    scheme = defaultScheme,
                    modifier = primary,
                )

                PermissionState.Blocked -> SmallButton(
                    label = tr(language, "Buka pengaturan", "Open settings"),
                    filled = true,
                    onClick = onOpenSettings,
                    scheme = defaultScheme,
                    modifier = primary,
                )
            }
        }

        if (location != null && state.permission != PermissionState.Granted) {
            Text(
                text = tr(
                    language,
                    "Izin lokasi mati. Lokasi tersimpan tetap dipakai.",
                    "Location permission is off. Your saved location is still used.",
                ),
                style = MaterialTheme.typography.bodySmall,
                color = defaultScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SmallButton(
    label: String,
    filled: Boolean,
    onClick: () -> Unit,
    scheme: ColorScheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // A disabled button uses the tonal style, so its text stays readable.
    val solid = filled && enabled
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(if (solid) scheme.onSurface else scheme.onSurface.copy(alpha = 0.08f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = when {
                !enabled -> scheme.onSurfaceVariant.copy(alpha = 0.6f)
                solid -> scheme.background
                else -> scheme.onSurface
            },
            maxLines = 1,
        )
    }
}
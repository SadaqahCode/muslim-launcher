package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.SensorStatus
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr


@Composable
internal fun CalibrationDialog(
    language: String,
    sensor: SensorStatus,
    onDismiss: () -> Unit,
    defaultScheme: ColorScheme,
) {
    val unavailable = sensor == SensorStatus.Unavailable

    ScrimDialog(
        onDismiss = onDismiss,
        maxWidth = 340.dp,
    ) {
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
                    text = if (unavailable) {
                        tr(language, "Kompas tidak tersedia", "Compass not available")
                    } else {
                        tr(language, "Kalibrasi kompas", "Calibrate your compass")
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = defaultScheme.onSurface,
                )

                if (unavailable) {
                    Text(
                        text = tr(
                            language,
                            "Sensor kompas tidak terbaca di HP ini. Tidak semua HP punya sensor kompas. Sudut kiblat dari utara tetap ditampilkan, jadi kamu bisa memakainya dengan kompas lain.",
                            "No compass reading from this phone. Not every phone has a compass sensor. The Qibla angle from north is still shown, so you can use it with another compass.",
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = defaultScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = tr(
                            language,
                            "1. Jauhkan HP dari magnet, logam, dan casing bermagnet.\n2. Putar dan gerakkan HP membentuk angka 8 beberapa kali.\n3. Letakkan HP mendatar.",
                            "1. Keep your phone away from magnets, metal, and magnetic cases.\n2. Move and rotate it in a figure 8 a few times.\n3. Hold it flat.",
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = defaultScheme.onSurfaceVariant,
                    )
                    Text(
                        text = when (sensor) {
                            SensorStatus.Checking -> tr(language, "Membaca sensor…", "Reading the sensor…")
                            SensorStatus.NeedsCalibration -> tr(language, "Akurasi masih rendah", "Accuracy is still low")
                            else -> tr(language, "Sensor tidak melaporkan akurasi rendah", "The sensor is not reporting low accuracy")
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = defaultScheme.onSurface,
                    )
                    Text(
                        text = tr(
                            language,
                            "Tidak semua HP punya sensor kompas, dan hasil sensor bisa meleset. Cocokkan dengan arah kiblat masjid terdekat bila ragu.",
                            "Not every phone has a compass, and readings can be off. If in doubt, compare with the Qibla at a nearby mosque.",
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = defaultScheme.onSurfaceVariant,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(tr(language, "Mengerti", "Got it"))
                    }
                }
            }
        }
    }
}
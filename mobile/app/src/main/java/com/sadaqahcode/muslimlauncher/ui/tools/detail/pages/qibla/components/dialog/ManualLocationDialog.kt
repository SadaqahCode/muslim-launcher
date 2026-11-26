package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.components.ScrimDialog
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr

@Composable
fun ManualLocationDialog(
    language: String,
    onDismiss: () -> Unit,
    onConfirm: (lat: Double, lon: Double, label: String?) -> Boolean,
    defaultScheme: ColorScheme,
) {
    var latText by remember { mutableStateOf("") }
    var lonText by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    ScrimDialog(
        onDismiss = onDismiss,
        maxWidth = 300.dp
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = defaultScheme.surface,
            contentColor = defaultScheme.onSurface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = tr(language, "Lokasi manual", "Manual location"),
                    style = MaterialTheme.typography.titleLarge,
                    color = defaultScheme.onSurface,
                )

                OutlinedTextField(
                    value = latText,
                    onValueChange = { latText = it; error = false },
                    label = {
                        Text(
                            tr(
                                language,
                                "Lintang (contoh -6.2088)",
                                "Latitude (e.g. -6.2088)",
                            )
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { lonText = it; error = false },
                    label = {
                        Text(
                            tr(
                                language,
                                "Bujur (contoh 106.8456)",
                                "Longitude (e.g. 106.8456)",
                            )
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(tr(language, "Nama (opsional)", "Name (optional)")) },
                    singleLine = true,
                )

                if (error) {
                    Text(
                        text = tr(
                            language,
                            "Koordinat tidak valid. Lintang -90 sampai 90, bujur -180 sampai 180.",
                            "Invalid coordinates. Latitude -90 to 90, longitude -180 to 180.",
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = defaultScheme.error,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(tr(language, "Batal", "Cancel"))
                    }
                    TextButton(onClick = {
                        val lat = latText.trim().replace(',', '.').toDoubleOrNull()
                        val lon = lonText.trim().replace(',', '.').toDoubleOrNull()
                        error = lat == null || lon == null || !onConfirm(lat, lon, label)
                    }) {
                        Text(tr(language, "Simpan", "Save"))
                    }
                }
            }
        }
    }
}
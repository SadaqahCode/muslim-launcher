package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.common.layout.rememberSoftScrollbarSettings
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.tr
import my.nanihadesuka.compose.LazyColumnScrollbar

@Composable
fun QiblaGuideContent(
    language: String,
    onClose: (() -> Unit)?,
    defaultScheme: ColorScheme,
    modifier: Modifier = Modifier,
) {
    val softScrollbar = rememberSoftScrollbarSettings()
    val listState = rememberLazyListState()

    Surface(
        color = defaultScheme.background,
        contentColor = defaultScheme.onBackground,
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxSize()) {

            // header
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onClose != null) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                            contentDescription = tr(language, "Tutup", "Close"),
                            tint = defaultScheme.onSurface,
                        )
                    }
                }
                Text(
                    text = tr(language, "Panduan kiblat", "Qibla guide"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = defaultScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (onClose == null) 16.dp else 4.dp, end = 16.dp),
                )
            }

            LazyColumnScrollbar(
                state = listState,
                settings = softScrollbar,
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        GuideSection(
                            title = tr(language, "Cara memakai", "How to use"),
                            body = tr(
                                language,
                                "1. Letakkan perangkat mendatar, jauh dari logam dan magnet.\n" +
                                        "2. Pegang sejajar dengan tanah lalu putar perlahan.\n" +
                                        "3. Ikuti panah sampai warnanya penuh dan teks berubah menjadi “Menghadap kiblat”.",
                                "1. Lay the device flat, away from metal and magnets.\n" +
                                        "2. Hold it level and turn slowly.\n" +
                                        "3. Follow the arrow until it fills and the text reads “Facing the Qibla”.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(
                                language,
                                "Kalibrasi: gerakkan membentuk angka 8",
                                "Calibrate: move in a figure 8",
                            ),
                            body = tr(
                                language,
                                "Sensor kompas adalah sensor magnetik. Logam, casing bermagnet, meja besi, dan casing HP lain bisa membuat arahnya melenceng beberapa derajat — kadang sampai puluhan derajat.\n\n" +
                                        "Cara paling cepat memperbaikinya: pegang HP dan gerakkan membentuk angka 8 di udara, pelan dan rata, sekitar 3–5 kali. Ini sama seperti saat Google Maps meminta kalibrasi kompas.\n\n" +
                                        "Lakukan setiap kali arah terasa janggal, atau setelah meletakkan HP di dekat benda logam/magnet.",
                                "The compass is a magnetic sensor. Metal, magnetic cases, steel desks, and nearby phone cases can throw the reading off by several degrees — sometimes tens of degrees.\n\n" +
                                        "The fastest fix: hold the phone and move it in a figure-8 pattern in the air, slowly and level, 3–5 times. This is the same motion Google Maps asks for when calibrating its compass.\n\n" +
                                        "Do this whenever the direction feels off, or after the phone was near metal or magnets.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(language, "Kenapa perlu GPS", "Why location is needed"),
                            body = tr(
                                language,
                                "Arah kiblat berbeda tergantung di mana kamu berada. Dari Jakarta, kiblat sekitar 295° dari utara; dari London, sekitar 119°. Tanpa tahu posisi, aplikasi tidak bisa menghitung arah.\n\n" +
                                        "Kami hanya memakai lokasi perkiraan (akurasi ±2 km), sekali, untuk menghitung arah. Selisih 2 km hanya mengubah arah sekitar 0,01° — jauh di bawah toleransi kompas. Lokasi disimpan di HP ini saja; tidak ada pelacakan, tidak ada riwayat, tidak dikirim ke server.",
                                "The Qibla direction depends on where you are. From Jakarta it is about 295° from north; from London, about 119°. Without your position, the app cannot compute the direction.\n\n" +
                                        "We only use an approximate location (±2 km accuracy), once, to work out the direction. A 2 km error changes the bearing by about 0.01° — far below compass tolerance. The location is stored on this phone only; no tracking, no history, not sent to any server.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(language, "Baterai dan data", "Battery and data"),
                            body = tr(
                                language,
                                "Lokasi hanya diminta saat kamu menekan Perbarui, atau saat halaman kiblat dibuka setelah lebih dari 30 hari. Tidak ada permintaan lokasi di latar belakang, jadi tidak menguras baterai.\n\n" +
                                        "Kompas memakai sensor rotasi HP. Sensor ini aktif hanya saat halaman ini terbuka, dan berhenti otomatis saat kamu pindah halaman. Tidak ada penggunaan data internet untuk menentukan arah.",
                                "Location is requested only when you tap Refresh, or when the Qibla page is opened after more than 30 days. There is no background location request, so battery drain is negligible.\n\n" +
                                        "The compass uses your phone's rotation sensor. It is active only while this page is open, and stops automatically when you navigate away. No internet data is used to compute the direction.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(
                                language,
                                "Jika HP tidak punya Google Play Services",
                                "If your phone has no Google Play Services",
                            ),
                            body = tr(
                                language,
                                "Aplikasi ini mencoba dua jalur untuk mengambil lokasi: Google Play Services (lebih cepat dan lebih stabil), dan Location Manager bawaan Android (cadangan).\n\n" +
                                        "Di HP tanpa Google Play Services — misalnya Huawei/Honor terbaru atau ROM kustom — jalur pertama tidak tersedia, dan aplikasi memakai jalur kedua secara otomatis. Di beberapa perangkat, jalur cadangan bisa lebih lambat atau memakai lokasi yang sedikit kurang akurat, tetapi arah kiblat tetap bisa dihitung.\n\n" +
                                        "Bila lokasi tetap tidak muncul, pastikan izin lokasi diberikan dan GPS HP menyala, lalu coba lagi di area terbuka.",
                                "This app tries two paths to fetch your location: Google Play Services (faster, more stable) and Android's built-in Location Manager (fallback).\n\n" +
                                        "On phones without Google Play Services — such as recent Huawei/Honor devices or custom ROMs — the first path is unavailable, and the app uses the second automatically. On some devices the fallback may be slower or slightly less accurate, but the Qibla direction can still be computed.\n\n" +
                                        "If location still does not appear, make sure location permission is granted and GPS is on, then try again in an open area.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(language, "Toleransi arah", "Direction tolerance"),
                            body = tr(
                                language,
                                "Untuk salat, tidak harus tepat mengenai Ka'bah. Menghadap ke arah umum kiblat (sekitar ±5°) sudah diterima oleh mayoritas ulama. Gunakan kompas sebagai panduan, bukan alat ukur presisi.",
                                "For prayer you do not need to aim exactly at the Kaaba. Facing the general direction of the Qibla (about ±5°) is accepted by most scholars. Treat the compass as a guide, not a precision instrument.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(language, "Jika kompas tidak tersedia", "If there is no compass"),
                            body = tr(
                                language,
                                "Beberapa perangkat tidak memiliki sensor kompas. Bila begitu, arah kiblat tetap ditampilkan sebagai derajat dari utara. Gunakan aplikasi peta untuk menemukan arah utara, lalu putar perangkat secara manual.",
                                "Some devices have no compass sensor. In that case the Qibla direction is still shown as degrees from north. Use a map app to find north, then rotate the device manually.",
                            ),
                            scheme = defaultScheme,
                        )
                    }

                    item {
                        GuideSection(
                            title = tr(language, "Bila arah terasa salah", "If the direction feels wrong"),
                            body = tr(
                                language,
                                "1. Pastikan lokasi di kartu bawah kompas adalah kota yang benar. Jika salah, ketuk Perbarui saat kamu berada di lokasi yang benar.\n" +
                                        "2. Kalibrasi ulang kompas dengan gerakan angka 8.\n" +
                                        "3. Jauhkan HP dari casing bermagnet, speaker, atau meja logam.\n" +
                                        "4. Bandingkan dengan arah kiblat masjid terdekat bila masih ragu.",
                                "1. Check that the location on the card below the compass is the right city. If not, tap Refresh while you are at the correct location.\n" +
                                        "2. Recalibrate the compass with the figure-8 motion.\n" +
                                        "3. Keep the phone away from magnetic cases, speakers, or metal desks.\n" +
                                        "4. If still in doubt, compare with the Qibla at a nearby mosque.",
                            ),
                            scheme = defaultScheme,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    body: String,
    scheme: ColorScheme,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
        )
    }
}
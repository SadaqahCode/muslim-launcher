package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla.components.CompassLabels

fun tr(language: String, id: String, en: String) = if (language == "id") id else en

fun statusText(state: QiblaUiState, language: String): String = when {
    state.location == null -> tr(language, "Tentukan lokasi dulu", "Set your location first")
    state.atKaaba -> tr(language, "Kamu berada di dekat Ka'bah", "You are right next to the Kaaba")
    state.qiblaBearing == null -> tr(language, "Menghitung arah…", "Working out the direction…")
    state.sensor == SensorStatus.Unavailable -> tr(
        language,
        "Sensor kompas tidak terbaca. Kiblat ${state.qiblaBearing.toInt()}° dari utara.",
        "No compass reading. Qibla is ${state.qiblaBearing.toInt()}° from north.",
    )
    state.sensor == SensorStatus.Checking -> tr(language, "Membaca sensor…", "Reading the sensor…")
    !state.flat -> tr(language, "Letakkan perangkat mendatar", "Hold your device flat")
    state.aligned -> tr(language, "Menghadap kiblat", "Facing the Qibla")
    else -> tr(language, "Putar perangkat", "Turn your device")
}

fun compassLabels(language: String) =
    if (language == "id") CompassLabels("U", "T", "S", "B") else CompassLabels("N", "E", "S", "W")

fun compassName(language: String, deg: Float): String {
    val index = QiblaCalculator.compassIndex(deg)
    val id = listOf("U", "TL", "T", "TG", "S", "BD", "B", "BL")
    val en = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    return if (language == "id") id[index] else en[index]
}
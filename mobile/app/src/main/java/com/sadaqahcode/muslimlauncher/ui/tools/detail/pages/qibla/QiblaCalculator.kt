package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pure math (no Android, no internet), so it is easy to test.
 * All angles in degrees, 0 = north, clockwise.
 */
object QiblaCalculator {
    const val KAABA_LAT = 21.422487
    const val KAABA_LON = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0

    /** Initial bearing from this point to the Ka'bah, from true north, 0..360. */
    fun bearing(lat: Double, lon: Double): Float {
        val p1 = Math.toRadians(lat)
        val p2 = Math.toRadians(KAABA_LAT)
        val dl = Math.toRadians(KAABA_LON - lon)
        val y = sin(dl) * cos(p2)
        val x = cos(p1) * sin(p2) - sin(p1) * cos(p2) * cos(dl)
        return normalize(Math.toDegrees(atan2(y, x)).toFloat())
    }

    /** Great-circle distance to the Ka'bah in km (haversine). */
    fun distanceKm(lat: Double, lon: Double): Int {
        val p1 = Math.toRadians(lat)
        val p2 = Math.toRadians(KAABA_LAT)
        val dp = p2 - p1
        val dl = Math.toRadians(KAABA_LON - lon)
        val a = sin(dp / 2) * sin(dp / 2) + cos(p1) * cos(p2) * sin(dl / 2) * sin(dl / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (EARTH_RADIUS_KM * c).roundToInt()
    }

    /** Clamp into the 0..360 range. */
    fun normalize(deg: Float): Float = ((deg % 360f) + 360f) % 360f

    /**
     * Shortest signed difference from [from] to [to], in -180..180.
     * 359 to 1 = +2, not -358. Used so the compass animation doesn't spin wildly.
     */
    fun shortestDelta(from: Float, to: Float): Float = (((to - from) % 360f) + 540f) % 360f - 180f

    /** Index of the 8 compass points: 0=N, 1=NE, 2=E, 3=SE, 4=S, 5=SW, 6=W, 7=NW. */
    fun compassIndex(deg: Float): Int = ((normalize(deg) + 22.5f) / 45f).toInt() % 8
}
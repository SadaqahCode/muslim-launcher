package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.Surface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param degrees   device azimuth relative to MAGNETIC NORTH, already smoothed. 0..360.
 *                  Declination correction (to true north) is done in the ViewModel.
 * @param accuracy  SensorManager.SENSOR_STATUS_* (0 = unreliable ... 3 = high).
 *                  Taken from the magnetometer once it reports.
 * @param flat      true when the device is close enough to horizontal (tilt < ~45°).
 *                  When held upright the azimuth is unstable, so [degrees] is just
 *                  the last good value.
 */
data class Heading(val degrees: Float, val accuracy: Int, val flat: Boolean)

enum class SensorKind { RotationVector, AccelMagnetic, None }

/**
 * Device heading from the built-in sensors, without Google Play Services.
 * A magnetometer is required. Order: TYPE_ROTATION_VECTOR, then
 * accelerometer + magnetometer, then unavailable.
 *
 * Sensors only run while [headings] is being collected (callbackFlow + awaitClose).
 */
@Singleton
class HeadingSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val sensorManager: SensorManager? = context.getSystemService(SensorManager::class.java)

    val kind: SensorKind
        get() {
            val sm = sensorManager ?: return SensorKind.None
            // Without a magnetometer there is no reference to north.
            if (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) return SensorKind.None
            return when {
                sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null -> SensorKind.RotationVector
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null -> SensorKind.AccelMagnetic
                else -> SensorKind.None
            }
        }

    fun headings(): Flow<Heading> = callbackFlow {
        val sm = sensorManager
        val sensorKind = kind
        if (sm == null || sensorKind == SensorKind.None) {
            close()
            return@callbackFlow
        }

        val displayManager = context.getSystemService(DisplayManager::class.java)

        val matrix = FloatArray(9)
        val remapped = FloatArray(9)
        val orientation = FloatArray(3)
        val rotationVector = FloatArray(4)
        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var haveGravity = false
        var haveGeomagnetic = false
        // Assume good until a sensor reports otherwise.
        var rotationAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        var magneticAccuracy: Int? = null
        val smoother = CircularSmoother(alpha = 0.18f)

        fun emitFromMatrix() {
            val accuracy = magneticAccuracy ?: rotationAccuracy
            // matrix[8] = Z axis of the device relative to world vertical; 1 = horizontal facing up.
            val flat = matrix[8] >= FLAT_MIN_COS

            val rotation = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_0
            val (axisX, axisY) = when (rotation) {
                Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
            }
            SensorManager.remapCoordinateSystem(matrix, axisX, axisY, remapped)
            SensorManager.getOrientation(remapped, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()

            if (flat) {
                trySend(Heading(smoother.add(azimuth), accuracy, flat = true))
            } else {
                // Do not feed unstable azimuths into the smoother.
                trySend(Heading(smoother.current ?: QiblaCalculator.normalize(azimuth), accuracy, flat = false))
            }
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        val values = event.values
                        if (values.size > 4) {
                            // Some older devices send more than 4 values and break the conversion.
                            System.arraycopy(values, 0, rotationVector, 0, 4)
                            SensorManager.getRotationMatrixFromVector(matrix, rotationVector)
                        } else {
                            SensorManager.getRotationMatrixFromVector(matrix, values)
                        }
                        emitFromMatrix()
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        lowPass(event.values, gravity, haveGravity)
                        haveGravity = true
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        magneticAccuracy = event.accuracy
                        // In RotationVector mode this sensor is only used for its accuracy.
                        if (sensorKind == SensorKind.AccelMagnetic) {
                            lowPass(event.values, geomagnetic, haveGeomagnetic)
                            haveGeomagnetic = true
                            if (haveGravity && SensorManager.getRotationMatrix(matrix, null, gravity, geomagnetic)) {
                                emitFromMatrix()
                            }
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, newAccuracy: Int) {
                when (sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> rotationAccuracy = newAccuracy
                    Sensor.TYPE_MAGNETIC_FIELD -> magneticAccuracy = newAccuracy
                }
            }
        }

        fun register(type: Int): Boolean =
            sm.registerListener(listener, sm.getDefaultSensor(type), SensorManager.SENSOR_DELAY_UI)

        val registered = when (sensorKind) {
            SensorKind.RotationVector -> {
                val ok = register(Sensor.TYPE_ROTATION_VECTOR)
                if (ok) register(Sensor.TYPE_MAGNETIC_FIELD) // optional, for accuracy
                ok
            }

            SensorKind.AccelMagnetic -> {
                val a = register(Sensor.TYPE_ACCELEROMETER)
                val m = register(Sensor.TYPE_MAGNETIC_FIELD)
                a && m
            }

            SensorKind.None -> false
        }
        if (!registered) close()

        awaitClose { sm.unregisterListener(listener) }
    }.conflate()

    private fun lowPass(input: FloatArray, output: FloatArray, initialized: Boolean) {
        if (!initialized) {
            for (i in 0 until 3) output[i] = input[i]
        } else {
            for (i in 0 until 3) output[i] += SENSOR_ALPHA * (input[i] - output[i])
        }
    }

    private companion object {
        const val FLAT_MIN_COS = 0.7f
        const val SENSOR_ALPHA = 0.15f
    }
}

/**
 * Circular angle smoother (mean of sin/cos), not a plain average:
 * a plain average of 359° and 1° gives 180°, which is wrong.
 */
private class CircularSmoother(private val alpha: Float) {
    private var s = 0f
    private var c = 0f

    var current: Float? = null
        private set

    fun add(deg: Float): Float {
        val rad = Math.toRadians(deg.toDouble())
        val ns = sin(rad).toFloat()
        val nc = cos(rad).toFloat()
        if (current == null) {
            s = ns
            c = nc
        } else {
            s += alpha * (ns - s)
            c += alpha * (nc - c)
        }
        val out = QiblaCalculator.normalize(Math.toDegrees(atan2(s.toDouble(), c.toDouble())).toFloat())
        current = out
        return out
    }
}
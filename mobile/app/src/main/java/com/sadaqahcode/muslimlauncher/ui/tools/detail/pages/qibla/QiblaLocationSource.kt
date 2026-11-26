package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.flagodna.debugprint.debugPrint
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "QiblaLocationSource"

data class GeoPoint(val lat: Double, val lon: Double, val altitude: Double = 0.0)

enum class LocationOrigin { Device, Manual }

data class SavedLocation(
    val point: GeoPoint,
    val label: String?,
    val savedAtMillis: Long,
    val origin: LocationOrigin,
)

sealed interface RefreshResult {
    data class Success(val location: SavedLocation) : RefreshResult
    data object NoPermission : RefreshResult
    data object LocationOff : RefreshResult
    data object Unavailable : RefreshResult
}

private val Context.qiblaDataStore: DataStore<Preferences> by preferencesDataStore(name = "qibla_location")

@Singleton
class QiblaLocationSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val Lat = doublePreferencesKey("lat")
        val Lon = doublePreferencesKey("lon")
        val Alt = doublePreferencesKey("alt")
        val Label = stringPreferencesKey("label")
        val SavedAt = longPreferencesKey("saved_at")
        val Origin = stringPreferencesKey("origin")
    }

    val saved: Flow<SavedLocation?> = context.qiblaDataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { p ->
            val lat = p[Keys.Lat]
            val lon = p[Keys.Lon]
            if (lat == null || lon == null) {
                null
            } else {
                SavedLocation(
                    point = GeoPoint(lat, lon, p[Keys.Alt] ?: 0.0),
                    label = p[Keys.Label],
                    savedAtMillis = p[Keys.SavedAt] ?: 0L,
                    origin = if (p[Keys.Origin] == LocationOrigin.Manual.name) {
                        LocationOrigin.Manual
                    } else {
                        LocationOrigin.Device
                    },
                )
            }
        }
        .distinctUntilChanged()

    suspend fun save(
        point: GeoPoint,
        label: String?,
        origin: LocationOrigin,
        savedAtMillis: Long = System.currentTimeMillis(),
    ): SavedLocation {
        val value = SavedLocation(point, label?.takeIf { it.isNotBlank() }, savedAtMillis, origin)
        debugPrint.d(TAG, "save point=$point label=$label origin=$origin savedAt=$savedAtMillis")
        context.qiblaDataStore.edit { p ->
            p[Keys.Lat] = point.lat
            p[Keys.Lon] = point.lon
            p[Keys.Alt] = point.altitude
            if (value.label != null) p[Keys.Label] = value.label else p.remove(Keys.Label)
            p[Keys.SavedAt] = value.savedAtMillis
            p[Keys.Origin] = origin.name
        }
        return value
    }

    // ── Permissions ───────────────────────────────────────────────────────

    fun hasFinePermission(): Boolean =
        granted(Manifest.permission.ACCESS_FINE_LOCATION)

    fun hasCoarsePermission(): Boolean =
        granted(Manifest.permission.ACCESS_COARSE_LOCATION)

    /** Either permission is enough to call LocationManager. */
    fun hasPermission(): Boolean = hasFinePermission() || hasCoarsePermission()

    fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(LocationManager::class.java) ?: return false
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    // ── GMS ───────────────────────────────────────────────────────────────

    private val gmsAvailable: Boolean by lazy {
        try {
            GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } catch (_: Exception) {
            false
        }
    }

    private val gmsClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Request a live fix. [timeoutMs] is the budget for both paths. Pass a larger value
     * for the first refresh after the user enables Location — GPS cold start needs time.
     */
    suspend fun refresh(timeoutMs: Long = DEFAULT_TIMEOUT_MS): RefreshResult {
        debugPrint.d(TAG, "refresh() start timeoutMs=$timeoutMs")
        if (!hasPermission()) {
            debugPrint.d(TAG, "refresh() → NoPermission")
            return RefreshResult.NoPermission
        }
        val lm = context.getSystemService(LocationManager::class.java) ?: run {
            debugPrint.d(TAG, "refresh() → Unavailable (no LocationManager)")
            return RefreshResult.Unavailable
        }
        if (!LocationManagerCompat.isLocationEnabled(lm)) {
            debugPrint.d(TAG, "refresh() → LocationOff")
            return RefreshResult.LocationOff
        }

        val fine = hasFinePermission()

        val candidates = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
            if (fine) add(LocationManager.GPS_PROVIDER)
        }
        val providers = candidates.filter { provider ->
            runCatching { lm.isProviderEnabled(provider) }.getOrDefault(false)
        }

        debugPrint.d(
            TAG,
            "refresh() fine=$fine gmsAvailable=$gmsAvailable " +
                    "candidates=$candidates enabled=$providers timeoutMs=$timeoutMs",
        )

        if (!gmsAvailable && providers.isEmpty()) {
            debugPrint.d(TAG, "refresh() → Unavailable (no GMS and no enabled providers)")
            return RefreshResult.Unavailable
        }

        val fresh: Location? = withTimeoutOrNull(timeoutMs.milliseconds) {
            coroutineScope {
                val flows = buildList {
                    if (gmsAvailable) {
                        add(flow {
                            debugPrint.d(TAG, "FLP → request fine=$fine timeout=$timeoutMs")
                            emit(refreshWithGms(fine, timeoutMs))
                        })
                    }
                    if (providers.isNotEmpty()) {
                        add(flow {
                            debugPrint.d(TAG, "bare → firstFix providers=$providers")
                            emit(firstFix(lm, providers))
                        })
                    }
                }
                flows.merge().filterNotNull().firstOrNull()
            }
        }

        if (fresh == null) {
            debugPrint.d(TAG, "refresh() → Unavailable (no fresh fix within ${timeoutMs}ms)")
            return RefreshResult.Unavailable
        }

        debugPrint.d(
            TAG,
            "refresh() fresh fix provider=${fresh.provider} " +
                    "lat=${fresh.latitude} lon=${fresh.longitude} " +
                    "accuracy=${if (fresh.hasAccuracy()) fresh.accuracy else -1f} " +
                    "ageMs=${ageMillis(fresh)}",
        )

        val point = GeoPoint(
            lat = fresh.latitude,
            lon = fresh.longitude,
            altitude = if (fresh.hasAltitude()) fresh.altitude else 0.0,
        )
        return RefreshResult.Success(
            location = save(point, label = null, origin = LocationOrigin.Device),
        )
    }

    private suspend fun refreshWithGms(fine: Boolean, timeoutMs: Long): Location? =
        suspendCancellableCoroutine { cont ->
            val priority = if (fine) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }
            val request = CurrentLocationRequest.Builder()
                .setPriority(priority)
                .setMaxUpdateAgeMillis(0)
                .setDurationMillis(timeoutMs)
                .build()
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation {
                debugPrint.d(TAG, "FLP → cancelled")
                cts.cancel()
            }
            try {
                gmsClient.getCurrentLocation(request, cts.token)
                    .addOnSuccessListener { loc: Location? ->
                        if (cont.isActive) {
                            debugPrint.d(
                                TAG,
                                "FLP → " + (loc?.let {
                                    "provider=${it.provider} lat=${it.latitude} lon=${it.longitude}"
                                } ?: "null"),
                            )
                            cont.resume(loc)
                        }
                    }
                    .addOnFailureListener { e ->
                        debugPrint.e(TAG, "FLP → failure ${e.message}")
                        if (cont.isActive) cont.resume(null)
                    }
            } catch (e: SecurityException) {
                debugPrint.e(TAG, "FLP → SecurityException ${e.message}")
                if (cont.isActive) cont.resume(null)
            } catch (e: Exception) {
                debugPrint.e(TAG, "FLP → Exception ${e.message}")
                if (cont.isActive) cont.resume(null)
            }
        }

    private suspend fun firstFix(lm: LocationManager, providers: List<String>): Location? =
        providers
            .map { provider -> flow { emit(currentFix(lm, provider)) } }
            .merge()
            .filterNotNull()
            .firstOrNull()

    @SuppressLint("MissingPermission")
    private suspend fun currentFix(lm: LocationManager, provider: String): Location? =
        suspendCancellableCoroutine { cont ->
            debugPrint.d(TAG, "currentFix($provider) → request")
            val signal = CancellationSignal()
            cont.invokeOnCancellation {
                debugPrint.d(TAG, "currentFix($provider) → cancelled")
                signal.cancel()
            }
            try {
                LocationManagerCompat.getCurrentLocation(
                    lm,
                    provider,
                    signal,
                    ContextCompat.getMainExecutor(context),
                ) { loc: Location? ->
                    if (cont.isActive) {
                        debugPrint.d(
                            TAG,
                            "currentFix($provider) → " +
                                    (loc?.let { "lat=${it.latitude} lon=${it.longitude}" } ?: "null"),
                        )
                        cont.resume(loc)
                    }
                }
            } catch (e: Exception) {
                debugPrint.e(TAG, "currentFix($provider) exception: ${e.message}")
                if (cont.isActive) cont.resume(null)
            }
        }

    private fun ageMillis(location: Location): Long =
        (SystemClock.elapsedRealtimeNanos() - location.elapsedRealtimeNanos) / 1_000_000L

    private fun granted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    companion object {
        /** Default timeout. Cold start is handled by the caller passing a larger value. */
        const val DEFAULT_TIMEOUT_MS = 20_000L
    }
}
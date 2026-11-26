package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.qibla

import android.hardware.GeomagneticField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.data.local.store.SettingsDataStore
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

enum class PermissionState { Granted, NotGranted, Blocked }

enum class LocationPrecision { Fine, Coarse }

enum class SensorStatus { Checking, Ok, NeedsCalibration, Unavailable }

enum class LocationProblem { None, LocationOff, Failed }

enum class QiblaDialog { None, Permission, LocationOff, Calibration, PrecisionUpgrade, PermissionStatus }

data class QiblaUiState(
    val permission: PermissionState = PermissionState.NotGranted,
    val precision: LocationPrecision = LocationPrecision.Coarse,
    val dialog: QiblaDialog = QiblaDialog.None,

    val gpsEnabled: Boolean = false,
    val location: SavedLocation? = null,
    val locationBusy: Boolean = false,
    val locationProblem: LocationProblem = LocationProblem.None,

    val qiblaBearing: Float? = null,
    val distanceKm: Int? = null,

    val sensor: SensorStatus = SensorStatus.Checking,
    val flat: Boolean = true,
    val headingTrue: Float? = null,

    val dialRotation: Float = 0f,
    val aligned: Boolean = false,
) {
    val atKaaba: Boolean get() = distanceKm == 0
}

private const val TAG = "QiblaViewModel"

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val locationSource: QiblaLocationSource,
    private val headingSource: HeadingSource,
    private val settingsDs: SettingsDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(
        QiblaUiState(
            sensor = if (headingSource.kind == SensorKind.None) SensorStatus.Unavailable else SensorStatus.Checking,
        )
    )
    val state: StateFlow<QiblaUiState> = _state.asStateFlow()

    val settings: StateFlow<UserSettings> = settingsDs.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val _haptics = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val haptics: SharedFlow<Unit> = _haptics.asSharedFlow()

    private var headingJob: Job? = null
    private var declination = 0f
    private var permissionBlocked = false
    private var locationLoaded = false
    private var autoRefreshTried = false

    private var introShown = false
    private var calibrationPending = false
    private var awaitingAppSettings = false
    private var awaitingLocationSettings = false
    private var precisionPromptShown = false

    init {
        debugPrint.d(TAG, "init sensorKind=${headingSource.kind}")
        locationSource.saved
            .onEach {
                debugPrint.d(TAG, "saved flow emit location=$it")
                applyLocation(it)
            }
            .launchIn(viewModelScope)
        syncPermission()
    }

    fun onResume() {
        debugPrint.d(
            TAG,
            "onResume awaitingAppSettings=$awaitingAppSettings awaitingLocationSettings=$awaitingLocationSettings",
        )

        if (awaitingAppSettings) {
            awaitingAppSettings = false
            if (locationSource.hasPermission()) {
                debugPrint.d(TAG, "onResume from app settings: granted → onPermissionGranted")
                onPermissionGranted()
                return
            }
            debugPrint.d(TAG, "onResume from app settings: still not granted")
        }

        if (awaitingLocationSettings) {
            awaitingLocationSettings = false
            debugPrint.d(TAG, "onResume from location settings: manual only")
            autoRefreshTried = true   // suppress any auto-fetch
        }

        syncPermission()
    }

    private fun syncPermission() {
        val granted = locationSource.hasPermission()
        if (granted) permissionBlocked = false
        val nextPerm = when {
            granted -> PermissionState.Granted
            permissionBlocked -> PermissionState.Blocked
            else -> PermissionState.NotGranted
        }
        val nextPrecision = if (locationSource.hasFinePermission()) {
            LocationPrecision.Fine
        } else {
            LocationPrecision.Coarse
        }
        debugPrint.d(
            TAG,
            "syncPermission granted=$granted fine=${nextPrecision == LocationPrecision.Fine} " +
                    "blocked=$permissionBlocked → perm=$nextPerm precision=$nextPrecision",
        )
        _state.update { it.copy(permission = nextPerm, precision = nextPrecision) }
        maybeAutoStart()
    }

    fun onPermissionDenied(blocked: Boolean) {
        debugPrint.d(TAG, "onPermissionDenied blocked=$blocked")
        permissionBlocked = blocked
        dismissDialog()
        syncPermission()
    }

    fun onPermissionGranted() {
        debugPrint.d(TAG, "onPermissionGranted")
        calibrationPending = true
        dismissDialog()
        offerPrecisionUpgradeIfNeeded()
        syncPermission()
    }

    /**
     * Called from [onPermissionGranted] before [syncPermission] runs, so it
     * cannot rely on [QiblaUiState.permission]/[QiblaUiState.precision] — it
     * queries [QiblaLocationSource] directly.
     *
     * When it fires: sets [autoRefreshTried] = true so the immediately
     * following [syncPermission] → [maybeAutoStart] does NOT auto-fetch.
     * When it doesn't fire (already Fine): leaves [autoRefreshTried] = false
     * so maybeAutoStart can do the normal initial fetch.
     */
    private fun offerPrecisionUpgradeIfNeeded() {
        if (precisionPromptShown) return
        if (!locationSource.hasPermission()) return
        if (locationSource.hasFinePermission()) return
        if (_state.value.dialog != QiblaDialog.None) return

        precisionPromptShown = true
        autoRefreshTried = true
        debugPrint.d(TAG, "offerPrecisionUpgradeIfNeeded → show PrecisionUpgrade")
        _state.update { it.copy(dialog = QiblaDialog.PrecisionUpgrade) }
    }

    /** Called by the UI after [RequestPermission] for ACCESS_FINE_LOCATION. */
    fun onPrecisionResult(granted: Boolean) {
        debugPrint.d(TAG, "onPrecisionResult granted=$granted")
        dismissDialog()
        if (granted) {
            calibrationPending = true
        }
        syncPermission()
    }

    fun dismissDialog() {
        val current = _state.value.dialog
        if (current != QiblaDialog.None) debugPrint.d(TAG, "dismissDialog was=$current")
        _state.update { it.copy(dialog = QiblaDialog.None) }
    }

    fun showCalibrationGuide() {
        debugPrint.d(TAG, "showCalibrationGuide sensor=${_state.value.sensor}")
        _state.update { it.copy(dialog = QiblaDialog.Calibration) }
    }

    fun onOpenAppSettings() {
        debugPrint.d(TAG, "onOpenAppSettings")
        awaitingAppSettings = true
        dismissDialog()
    }

    fun onOpenLocationSettings() {
        debugPrint.d(TAG, "onOpenLocationSettings")
        awaitingLocationSettings = true
        dismissDialog()
    }

    private fun refreshGpsState() {
        val enabled = locationSource.isLocationEnabled()
        if (_state.value.gpsEnabled != enabled) {
            debugPrint.d(TAG, "refreshGpsState enabled=$enabled")
            _state.update { it.copy(gpsEnabled = enabled) }
        }
    }

    fun showPermissionStatus() {
        debugPrint.d(TAG, "showPermissionStatus")
        refreshGpsState()
        _state.update { it.copy(dialog = QiblaDialog.PermissionStatus) }
    }

    private fun showPendingCalibration() {
        if (!calibrationPending) return
        debugPrint.d(TAG, "showPendingCalibration firing")
        calibrationPending = false
        showCalibrationGuide()
    }

    fun onRefreshClick() {
        debugPrint.d(TAG, "onRefreshClick")
        restartHeading()
        refreshLocation(userInitiated = true)
    }

    /**
     * @param firstAfterEnable use when the user just enabled location or upgraded to Precise —
     * GPS cold start legitimately needs a longer budget than a warm refresh.
     */
    private fun refreshLocation(userInitiated: Boolean, firstAfterEnable: Boolean = false) {
        if (_state.value.locationBusy) {
            debugPrint.d(TAG, "refreshLocation skipped: already busy")
            return
        }
        val sourceTimeout = if (firstAfterEnable) FIRST_FIX_TIMEOUT_MS else QiblaLocationSource.DEFAULT_TIMEOUT_MS
        val safetyLimit = sourceTimeout + SAFETY_MARGIN_MS
        debugPrint.d(
            TAG,
            "refreshLocation start userInitiated=$userInitiated first=$firstAfterEnable " +
                    "sourceTimeout=$sourceTimeout safetyLimit=$safetyLimit",
        )
        _state.update { it.copy(locationBusy = true, locationProblem = LocationProblem.None) }
        viewModelScope.launch {
            var result: RefreshResult
            try {
                result = withTimeoutOrNull(safetyLimit.milliseconds) {
                    locationSource.refresh(timeoutMs = sourceTimeout)
                } ?: RefreshResult.Unavailable
            } catch (e: CancellationException) {
                debugPrint.d(TAG, "refreshLocation cancelled")
                throw e
            } catch (e: Exception) {
                debugPrint.e(TAG, "refreshLocation exception: ${e.message}")
                result = RefreshResult.Unavailable
            } finally {
                _state.update { it.copy(locationBusy = false) }
            }
            debugPrint.d(TAG, "refreshLocation result=$result")
            onRefreshResult(result, userInitiated)
        }
    }

    private fun onRefreshResult(result: RefreshResult, userInitiated: Boolean) {
        debugPrint.d(TAG, "onRefreshResult result=$result userInitiated=$userInitiated")
        when (result) {
            is RefreshResult.Success -> {
                debugPrint.d(TAG, "onRefreshResult success fresh")
                _state.update { it.copy(locationProblem = LocationProblem.None) }
                showPendingCalibration()
            }

            RefreshResult.NoPermission -> {
                debugPrint.d(TAG, "onRefreshResult no permission")
                syncPermission()
            }

            RefreshResult.LocationOff -> {
                val interrupt = userInitiated || _state.value.location == null
                debugPrint.d(TAG, "onRefreshResult location off interrupt=$interrupt")
                _state.update {
                    it.copy(
                        gpsEnabled = false,
                        locationProblem = LocationProblem.LocationOff,
                        dialog = if (interrupt) QiblaDialog.LocationOff else it.dialog,
                    )
                }
            }

            RefreshResult.Unavailable -> {
                val coarseOnly = _state.value.precision == LocationPrecision.Coarse
                val showUpgrade = coarseOnly && userInitiated
                debugPrint.d(
                    TAG,
                    "onRefreshResult unavailable coarseOnly=$coarseOnly showUpgrade=$showUpgrade",
                )
                _state.update {
                    it.copy(
                        locationProblem = LocationProblem.Failed,
                        dialog = if (showUpgrade) QiblaDialog.PrecisionUpgrade else it.dialog,
                    )
                }
            }
        }
    }

    /** @return false when the coordinates fall outside the valid range. */
    fun setManualLocation(lat: Double, lon: Double, label: String?): Boolean {
        debugPrint.d(TAG, "setManualLocation lat=$lat lon=$lon label=$label")
        if (lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            debugPrint.d(TAG, "setManualLocation rejected: out of range")
            return false
        }
        viewModelScope.launch {
            locationSource.save(GeoPoint(lat, lon), label, LocationOrigin.Manual)
            showPendingCalibration()
        }
        _state.update { it.copy(locationProblem = LocationProblem.None) }
        return true
    }

    private fun applyLocation(location: SavedLocation?) {
        debugPrint.d(TAG, "applyLocation location=$location")
        locationLoaded = true
        if (location == null) {
            declination = 0f
            _state.update {
                it.copy(location = null, qiblaBearing = null, distanceKm = null, aligned = false)
            }
        } else {
            val p = location.point
            declination = GeomagneticField(
                p.lat.toFloat(),
                p.lon.toFloat(),
                p.altitude.toFloat(),
                System.currentTimeMillis(),
            ).declination
            val distance = QiblaCalculator.distanceKm(p.lat, p.lon)
            val bearing = if (distance == 0) null else QiblaCalculator.bearing(p.lat, p.lon)
            debugPrint.d(
                TAG,
                "applyLocation declination=$declination distanceKm=$distance bearing=$bearing",
            )
            _state.update {
                it.copy(
                    location = location,
                    qiblaBearing = bearing,
                    distanceKm = distance,
                )
            }
        }
        maybeAutoStart()
    }

    private fun maybeAutoStart() {
        val s = _state.value
        if (!locationLoaded) {
            debugPrint.d(TAG, "maybeAutoStart skipped: location not loaded yet")
            return
        }

        if (s.permission != PermissionState.Granted) {
            if (s.location == null && !introShown) {
                debugPrint.d(TAG, "maybeAutoStart showing permission intro")
                introShown = true
                _state.update { it.copy(dialog = QiblaDialog.Permission) }
            }
            return
        }

        if (autoRefreshTried || s.locationBusy) {
            debugPrint.d(TAG, "maybeAutoStart skipped: tried=$autoRefreshTried busy=${s.locationBusy}")
            return
        }
        val loc = s.location
        val ageMs = loc?.let { System.currentTimeMillis() - it.savedAtMillis } ?: -1L
        val stale = loc != null &&
                loc.origin == LocationOrigin.Device &&
                ageMs > STALE_MS
        debugPrint.d(TAG, "maybeAutoStart loc=${loc?.origin} ageMs=$ageMs stale=$stale")
        if (loc == null || stale) {
            autoRefreshTried = true
            refreshLocation(userInitiated = false)
        }
    }

    // ── compass ──────────────────────────────────────────────────────────

    fun startHeading() {
        if (headingJob?.isActive == true) {
            debugPrint.d(TAG, "startHeading skipped: already active")
            return
        }
        if (headingSource.kind == SensorKind.None) {
            debugPrint.d(TAG, "startHeading no sensor available")
            markSensorUnavailable()
            return
        }
        debugPrint.d(TAG, "startHeading begin kind=${headingSource.kind}")
        headingJob = viewModelScope.launch {
            val watchdog = launch {
                delay(SENSOR_WAIT_MS.milliseconds)
                debugPrint.w(TAG, "startHeading watchdog fired: no data in ${SENSOR_WAIT_MS}ms")
                markSensorUnavailable()
            }
            headingSource.headings().collect {
                watchdog.cancel()
                onHeading(it)
            }
            watchdog.cancel()
            debugPrint.d(TAG, "startHeading headings flow ended")
            markSensorUnavailable()
        }
    }

    fun stopHeading() {
        if (headingJob == null) return
        debugPrint.d(TAG, "stopHeading")
        headingJob?.cancel()
        headingJob = null
    }

    private fun restartHeading() {
        debugPrint.d(TAG, "restartHeading")
        stopHeading()
        if (headingSource.kind != SensorKind.None) {
            _state.update { it.copy(sensor = SensorStatus.Checking, aligned = false) }
        }
        startHeading()
    }

    private fun markSensorUnavailable() {
        val was = _state.value.sensor
        if (was != SensorStatus.Unavailable) {
            debugPrint.d(TAG, "markSensorUnavailable was=$was")
        }
        _state.update { it.copy(sensor = SensorStatus.Unavailable, aligned = false) }
    }

    private fun onHeading(h: Heading) {
        val wasAligned = _state.value.aligned
        val wasSensor = _state.value.sensor
        val wasFlat = _state.value.flat
        _state.update { s ->
            val status = if (h.accuracy in 0..1) SensorStatus.NeedsCalibration else SensorStatus.Ok
            if (!h.flat) {
                return@update s.copy(sensor = status, flat = false)
            }
            val trueHeading = QiblaCalculator.normalize(h.degrees + declination)
            val dial = s.dialRotation + QiblaCalculator.shortestDelta(s.dialRotation, -trueHeading)
            val bearing = s.qiblaBearing
            val tolerance = if (s.aligned) ALIGN_EXIT_DEG else ALIGN_ENTER_DEG
            val aligned = bearing != null &&
                    abs(QiblaCalculator.shortestDelta(trueHeading, bearing)) <= tolerance
            s.copy(
                sensor = status,
                flat = true,
                headingTrue = trueHeading,
                dialRotation = dial,
                aligned = aligned,
            )
        }
        val now = _state.value
        if (now.aligned != wasAligned) {
            debugPrint.d(
                TAG,
                "onHeading aligned=$wasAligned→${now.aligned} heading=${now.headingTrue} bearing=${now.qiblaBearing}",
            )
        }
        if (now.sensor != wasSensor) {
            debugPrint.d(TAG, "onHeading sensor=$wasSensor→${now.sensor} accuracy=${h.accuracy}")
        }
        if (now.flat != wasFlat) {
            debugPrint.d(TAG, "onHeading flat=$wasFlat→${now.flat}")
        }
        if (!wasAligned && now.aligned) _haptics.tryEmit(Unit)
    }

    override fun onCleared() {
        debugPrint.d(TAG, "onCleared")
        super.onCleared()
        stopHeading()
    }

    private companion object {
        const val ALIGN_ENTER_DEG = 4f
        const val ALIGN_EXIT_DEG = 7f
        const val STALE_MS = 30L * 24 * 60 * 60 * 1000

        /** GPS cold start after Location toggle — allow up to 60s. */
        const val FIRST_FIX_TIMEOUT_MS = 60_000L

        /** Safety margin above the source's timeout. */
        const val SAFETY_MARGIN_MS = 5_000L

        const val SENSOR_WAIT_MS = 3_000L
    }
}
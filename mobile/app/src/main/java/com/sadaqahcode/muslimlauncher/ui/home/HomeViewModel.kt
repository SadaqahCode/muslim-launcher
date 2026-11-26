package com.sadaqahcode.muslimlauncher.ui.home

import android.app.Activity
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import android.provider.Settings
import androidx.compose.ui.unit.Density
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.data.repository.InstalledAppsRepository
import com.sadaqahcode.muslimlauncher.data.local.store.SettingsDataStore
import com.sadaqahcode.muslimlauncher.data.repository.AppPreferencesRepository
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.domain.usecase.SetCategoryUseCase
import com.sadaqahcode.muslimlauncher.domain.usecase.ToggleDockUseCase
import com.sadaqahcode.muslimlauncher.domain.usecase.TogglePinUseCase
import com.sadaqahcode.muslimlauncher.domain.usecase.ToggleResult
import com.sadaqahcode.muslimlauncher.service.LauncherAccessibilityService
import com.sadaqahcode.muslimlauncher.service.LauncherDeviceAdminReceiver
import com.sadaqahcode.muslimlauncher.service.NotificationShadeService
import com.sadaqahcode.muslimlauncher.ui.home.components.AccessibilityDialogKind
import com.sadaqahcode.muslimlauncher.ui.home.helper.AutoHomeLayout
import com.sadaqahcode.muslimlauncher.ui.home.helper.ShadeOpenResult
import com.sadaqahcode.muslimlauncher.ui.home.helper.isAccessibilityServiceEnabled
import com.sadaqahcode.muslimlauncher.ui.settings.isDarkColor
import com.sadaqahcode.muslimlauncher.ui.settings.isDarkGradient
import com.sadaqahcode.muslimlauncher.ui.settings.isDarkImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class PrayerHeroState(
    val hijriDate: String = "2 Jumada al-Thani 1448 AH",
    val gregorianDate: String = "Thursday, 12 November 2026",
    val nextPrayerName: String = "Asr",
    val nextPrayerTime: String = "14:43",
    val timeRemaining: String = "in 17m",
    val currentWindow: String = "Dhuhr",
    val prayerTimes: List<Pair<String, String>> = listOf(
        "Fajr" to "03:47",
        "Dhuhr" to "11:23",
        "Asr" to "14:43",
        "Maghrib" to "17:36",
        "Isha" to "18:50",
    ),
    val windowLine: String = "Dhuhr began 3h 03m ago · Surakarta (Solo)",
)

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    private val appRepository: InstalledAppsRepository,
    private val prefsRepository: AppPreferencesRepository,
    private val settingsDs: SettingsDataStore,
) : AndroidViewModel(app) {

    private val togglePinUseCase = TogglePinUseCase(prefsRepository)
    private val toggleDockUseCase = ToggleDockUseCase(prefsRepository)
    private val setCategoryUseCase = SetCategoryUseCase(prefsRepository)

    val heroState = MutableStateFlow(PrayerHeroState())

    private val _installedApps = MutableStateFlow<List<LauncherApp>>(emptyList())
    private val _loading = MutableStateFlow(true)
    private val _drawerVisible = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedApp = MutableStateFlow<LauncherApp?>(null)
    private val _message = MutableStateFlow<String?>(null)
    private val _showAccessibilityDialog = MutableStateFlow(false)
    private val _dialogKind = MutableStateFlow(AccessibilityDialogKind.Shade)

    private val _accessibilityEnabled = MutableStateFlow(false)
    private val _deviceAdminEnabled = MutableStateFlow(false)
    private val _notificationShadeReady = MutableStateFlow(false)
    private val _shadeAccessibilityEnabled = MutableStateFlow(false)

    private val _hasWorkProfile = MutableStateFlow(false)
    private val _isWorkPaused = MutableStateFlow(false)

    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    val drawerVisible: StateFlow<Boolean> = _drawerVisible.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val selectedApp: StateFlow<LauncherApp?> = _selectedApp.asStateFlow()
    val message: StateFlow<String?> = _message.asStateFlow()
    val showAccessibilityDialog: StateFlow<Boolean> = _showAccessibilityDialog.asStateFlow()
    val dialogKind: StateFlow<AccessibilityDialogKind> = _dialogKind.asStateFlow()
    val accessibilityEnabled: StateFlow<Boolean> = _accessibilityEnabled.asStateFlow()
    val deviceAdminEnabled: StateFlow<Boolean> = _deviceAdminEnabled.asStateFlow()
    val shadeAccessibilityEnabled: StateFlow<Boolean> = _shadeAccessibilityEnabled.asStateFlow()
    val hasWorkProfile: StateFlow<Boolean> = _hasWorkProfile.asStateFlow()
    val isWorkPaused: StateFlow<Boolean> = _isWorkPaused.asStateFlow()

    private data class ScreenSize(val widthDp: Int = 360, val heightDp: Int = 720)
    private val _screenSize = MutableStateFlow(ScreenSize())

    fun onScreenSizeChanged(widthDp: Int, heightDp: Int) {
        _screenSize.value = ScreenSize(widthDp, heightDp)
    }

    private val _imageIsDark = MutableStateFlow<Boolean?>(null)

    // Package listener - no service needed
    private var launcherAppsCallback: LauncherApps.Callback? = null

    val settings: StateFlow<UserSettings> = combine(
        settingsDs.settings,
        _screenSize,
    ) { raw, size ->
        val layout = AutoHomeLayout.resolve(raw, size.widthDp, size.heightDp)
        raw.copy(
            homeColumns = layout.homeColumns,
            drawerColumns = layout.drawerColumns,
            maxPinned = layout.maxPinned,
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val appsWithPrefs =
        combine(_installedApps, prefsRepository.observeAll()) { installed, prefMap ->
            installed.map { app ->
                val pref = prefMap[app.packageName to app.userId]
                app.copy(
                    category = pref?.category
                        ?.let { runCatching { AppCategory.valueOf(it) }.getOrNull() }
                        ?: AppCategory.DUNYA_OPTIONAL,
                    isPinned = pref?.isPinned ?: false,
                    isDocked = pref?.isDocked ?: false,
                )
            }
        }

    val homeApps: StateFlow<List<LauncherApp>> = appsWithPrefs
        .combine(settings) { apps, s ->
            apps.filter { it.isPinned }
                .sortedBy { it.label.lowercase() }
                .take(s.maxPinned)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dockAppList: StateFlow<List<LauncherApp>> = appsWithPrefs
        .map { apps -> apps.filter { it.isDocked }.sortedBy { it.label.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val drawerApps: StateFlow<List<LauncherApp>> = appsWithPrefs
        .combine(_searchQuery) { apps, q ->
            val filtered = if (q.isBlank()) apps
            else apps.filter { it.label.contains(q, ignoreCase = true) }
            filtered.sortedWith(
                compareBy<LauncherApp> { categoryRank(it.category) }
                    .thenBy { it.label.lowercase() }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workApps: StateFlow<List<LauncherApp>> = appsWithPrefs
        .map { apps -> apps.filter { it.isWork }.sortedBy { it.label.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val personalApps: StateFlow<List<LauncherApp>> = appsWithPrefs
        .map { apps -> apps.filter { !it.isWork }.sortedBy { it.label.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val wallpaperIsDark: StateFlow<Boolean> = settingsDs.settings
        .combine(_imageIsDark) { s, imageDark ->
            when (s.wallpaperMode) {
                WallpaperMode.Color -> isDarkColor(s.wallpaperColor)
                WallpaperMode.Gradient -> isDarkGradient(
                    s.wallpaperGradientStart, s.wallpaperGradientEnd,
                )
                WallpaperMode.Image -> imageDark ?: true
                WallpaperMode.System -> false
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            settingsDs.settings
                .map { it.iconShape }
                .distinctUntilChanged()
                .collectLatest { shape ->
                    _loading.value = true
                    val density = Density(
                        getApplication<Application>().resources.displayMetrics.density,
                        1f,
                    )
                    val loaded = withContext(Dispatchers.IO) {
                        appRepository.loadLaunchableApps(shape, density)
                    }
                    _installedApps.value = loaded
                    _loading.value = false
                    refreshWorkProfileState()
                }
        }

        viewModelScope.launch {
            settingsDs.settings
                .map { it.wallpaperImageUri }
                .distinctUntilChanged()
                .collectLatest { uri ->
                    _imageIsDark.value = if (uri != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            isDarkImage(getApplication(), uri)
                        } else {
                            true
                        }
                    } else {
                        null
                    }
                }
        }

        startPackageListener()
        refreshAccessibilityState()
        refreshWorkProfileState()
    }

    override fun onCleared() {
        val launcherApps = getApplication<Application>()
            .getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        launcherAppsCallback?.let { runCatching { launcherApps.unregisterCallback(it) } }
        launcherAppsCallback = null
        super.onCleared()
    }

    // ---- Package listener ----
    private fun startPackageListener() {
        val ctx = getApplication<Application>()
        val launcherApps = ctx.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val cb = object : LauncherApps.Callback() {
            override fun onPackageAdded(packageName: String, user: UserHandle) { reloadApps() }
            override fun onPackageRemoved(packageName: String, user: UserHandle) { reloadApps() }
            override fun onPackageChanged(packageName: String, user: UserHandle) { reloadApps() }
            override fun onPackagesAvailable(packageNames: Array<String>, user: UserHandle, replacing: Boolean) { reloadApps() }
            override fun onPackagesUnavailable(packageNames: Array<String>, user: UserHandle, replacing: Boolean) { reloadApps() }
        }
        launcherAppsCallback = cb
        runCatching { launcherApps.registerCallback(cb, Handler(Looper.getMainLooper())) }
    }

    private fun reloadApps() {
        viewModelScope.launch {
            _loading.value = true
            val shape = settings.value.iconShape
            val density = Density(
                getApplication<Application>().resources.displayMetrics.density,
                1f,
            )
            val loaded = withContext(Dispatchers.IO) {
                appRepository.loadLaunchableApps(shape, density)
            }
            _installedApps.value = loaded
            _loading.value = false
            refreshWorkProfileState()
        }
    }

    private fun categoryRank(c: AppCategory): Int = when (c) {
        AppCategory.DEEN -> 0
        AppCategory.DUNYA_NECESSARY -> 1
        AppCategory.DUNYA_OPTIONAL -> 2
        AppCategory.FITNAH_RISK -> 3
    }

    fun refreshWorkProfileState() {
        val ctx = getApplication<Application>()
        val userManager = ctx.getSystemService(Context.USER_SERVICE) as UserManager
        val workHandle = userManager.userProfiles.firstOrNull { isWorkProfile(userManager, it) }
        val hasWork = workHandle != null
        val paused = if (workHandle != null) {
            isQuietModeEnabledSafe(userManager, workHandle)
        } else false
        debugPrint.d(TAG, "workProfile: hasWork=$hasWork paused=$paused")
        _hasWorkProfile.value = hasWork
        _isWorkPaused.value = paused
    }

    fun setWorkPaused(paused: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _message.value = "Pausing work requires Android 9+"
            return
        }
        val ctx = getApplication<Application>()
        val userManager = ctx.getSystemService(Context.USER_SERVICE) as UserManager
        val workHandle = userManager.userProfiles.firstOrNull { isWorkProfile(userManager, it) } ?: return
        val ok = runCatching { requestQuietModeEnabledSafe(userManager, paused, workHandle) }.getOrDefault(false)
        debugPrint.d(TAG, "setWorkPaused($paused) result=$ok")
        if (ok) _isWorkPaused.value = paused
        else _message.value = "Set Muslim Launcher as default launcher to pause work"
    }

    fun toggleWorkPaused() = setWorkPaused(!_isWorkPaused.value)

    private fun isWorkProfile(userManager: UserManager, profile: UserHandle): Boolean {
        val userId = profile.hashCode()
        if (userId == Process.myUserHandle().hashCode()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return runCatching {
                UserManager::class.java
                    .getMethod("isManagedProfile", Int::class.javaPrimitiveType)
                    .invoke(userManager, userId) as? Boolean
            }.getOrNull() ?: (userId != 0)
        }
        return userId != 0
    }

    private fun isQuietModeEnabledSafe(userManager: UserManager, workHandle: UserHandle): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return runCatching {
            UserManager::class.java
                .getMethod("isQuietModeEnabled", UserHandle::class.java)
                .invoke(userManager, workHandle) as? Boolean
        }.getOrNull() ?: false
    }

    private fun requestQuietModeEnabledSafe(
        userManager: UserManager,
        enabled: Boolean,
        workHandle: UserHandle
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return runCatching {
            UserManager::class.java
                .getMethod(
                    "requestQuietModeEnabled",
                    Boolean::class.javaPrimitiveType,
                    UserHandle::class.java
                )
                .invoke(userManager, enabled, workHandle) as? Boolean
        }.getOrNull() ?: false
    }

    fun isDefaultLauncher(): Boolean {
        val app = getApplication<Application>()
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolve = app.packageManager.resolveActivity(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolve?.activityInfo?.packageName == app.packageName
    }

    fun isDeviceAdminActive(): Boolean {
        val ctx = getApplication<Application>()
        val component = ComponentName(ctx, LauncherDeviceAdminReceiver::class.java)
        val dpm = ctx.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isAdminActive(component)
    }

    fun refreshAccessibilityState() {
        val ctx = getApplication<Application>()
        val lockEnabled = isAccessibilityServiceEnabled(ctx, LauncherAccessibilityService::class.java)
        val shadeEnabled = isAccessibilityServiceEnabled(ctx, NotificationShadeService::class.java)
        val instanceRunning = LauncherAccessibilityService.instance != null
        val shadeRunning = NotificationShadeService.instance != null
        debugPrint.d(TAG, "refresh: lock=$lockEnabled shade=$shadeEnabled shadeRunning=$shadeRunning")
        _accessibilityEnabled.value = lockEnabled || instanceRunning
        _shadeAccessibilityEnabled.value = shadeEnabled || shadeRunning
        _deviceAdminEnabled.value = isDeviceAdminActive()
        _notificationShadeReady.value = shadeRunning
    }

    fun openAccessibilitySettings() {
        val ctx = getApplication<Application>()
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(intent)
    }

    fun dismissAccessibilityDialog() {
        _showAccessibilityDialog.value = false
    }

    fun onAccessibilityDialogConfirm() {
        _showAccessibilityDialog.value = false
        openAccessibilitySettings()
    }

    fun requestDeviceAdmin(activity: Activity) {
        val component = ComponentName(activity, LauncherDeviceAdminReceiver::class.java)
        val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (!dpm.isAdminActive(component)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Allow Muslim Launcher to lock screen on double-tap")
            }
            activity.startActivity(intent)
        }
    }

    fun lockWithDeviceAdmin(context: Context): Boolean {
        val component = ComponentName(context, LauncherDeviceAdminReceiver::class.java)
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (!dpm.isAdminActive(component)) return false
        dpm.lockNow()
        return true
    }

    fun openNotificationShade(context: Context): ShadeOpenResult {
        if (NotificationShadeService.instance != null) {
            return if (NotificationShadeService.expandNotificationShade())
                ShadeOpenResult.OpenedViaAccessibility
            else ShadeOpenResult.Failed
        }
        try {
            val statusBar = context.getSystemService("statusbar")
            val method = statusBar?.javaClass?.getMethod("expandNotificationsPanel")
            method?.invoke(statusBar)
            return ShadeOpenResult.OpenedSilently
        } catch (e: Exception) {
            debugPrint.d("Shade", "reflection failed: ${e::class.simpleName}")
            val enabled = isAccessibilityServiceEnabled(context, NotificationShadeService::class.java)
            return if (enabled) ShadeOpenResult.EnabledButNotBound
            else ShadeOpenResult.NeedsAccessibilityPermission
        }
    }

    fun onDoubleTapHome() {
        refreshAccessibilityState()
        debugPrint.d(TAG, "onDoubleTapHome called, sdk=${Build.VERSION.SDK_INT}, accessibility=${_accessibilityEnabled.value}, deviceAdmin=${_deviceAdminEnabled.value}")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            debugPrint.d(TAG, "ignored, API < 28")
            return
        }
        if (_accessibilityEnabled.value) {
            debugPrint.d(TAG, "calling lockScreen() via Accessibility")
            val locked = LauncherAccessibilityService.lockScreen()
            debugPrint.d(TAG, "lockScreen result=$locked")
            if (locked) return
        }
        if (_deviceAdminEnabled.value) {
            debugPrint.d(TAG, "calling lockNow() via Device Admin")
            val locked = lockWithDeviceAdmin(getApplication())
            debugPrint.d(TAG, "deviceAdmin lock result=$locked")
            if (locked) return
        }
        debugPrint.d(TAG, "both off, show dialog")
        _dialogKind.value = AccessibilityDialogKind.Lock
        _showAccessibilityDialog.value = true
    }

    fun requestAccessibility() {
        _dialogKind.value = AccessibilityDialogKind.Lock
        _showAccessibilityDialog.value = true
    }

    fun requestShadeAccessibility() {
        _dialogKind.value = AccessibilityDialogKind.Shade
        _showAccessibilityDialog.value = true
    }

    fun toggleFavorite(app: LauncherApp) {
        viewModelScope.launch {
            val max = settings.value.maxPinned
            when (val r = togglePinUseCase(app, max)) {
                is ToggleResult.Success -> Unit
                is ToggleResult.Rejected -> _message.value = r.reason
            }
            _selectedApp.value = null
        }
    }

    fun toggleDock(app: LauncherApp) {
        viewModelScope.launch {
            when (val r = toggleDockUseCase(app)) {
                is ToggleResult.Success -> Unit
                is ToggleResult.Rejected -> _message.value = r.reason
            }
            _selectedApp.value = null
        }
    }

    fun setCategory(app: LauncherApp, category: AppCategory) {
        viewModelScope.launch {
            setCategoryUseCase(app, category)
            _selectedApp.value = null
        }
    }

    fun consumeMessage() { _message.value = null }

    fun launch(app: LauncherApp) {
        val ctx = getApplication<Application>()
        if (app.isWork && app.userHandle != null && app.componentName.isNotBlank()) {
            val launcherApps = ctx.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val component = ComponentName.unflattenFromString(app.componentName) ?: return
            runCatching {
                launcherApps.startMainActivity(component, app.userHandle, null, null)
            }.onFailure { debugPrint.d(TAG, "launch work failed: ${it.message}") }
            return
        }
        val intent = ctx.packageManager.getLaunchIntentForPackage(app.packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    fun openDrawer() { _drawerVisible.value = true }
    fun closeDrawer() {
        _drawerVisible.value = false
        _searchQuery.value = ""
    }
    fun onSearchQueryChange(q: String) { _searchQuery.value = q }
    fun selectAppForMenu(app: LauncherApp) { _selectedApp.value = app }
    fun dismissMenu() { _selectedApp.value = null }

    fun openAppInfo(app: LauncherApp) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${app.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (app.isWork && app.userHandle != null) {
                putExtra(Intent.EXTRA_USER, app.userHandle)
            }
        }
        getApplication<Application>().startActivity(intent)
        _selectedApp.value = null
    }

    fun requestUninstall(app: LauncherApp) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:${app.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (app.isWork && app.userHandle != null) {
                putExtra(Intent.EXTRA_USER, app.userHandle)
            }
        }
        getApplication<Application>().startActivity(intent)
        _selectedApp.value = null
    }
}

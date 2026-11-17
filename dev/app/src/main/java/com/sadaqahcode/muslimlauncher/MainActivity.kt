package com.sadaqahcode.muslimlauncher

import android.app.WallpaperManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.data.local.SettingsDataStore
import com.sadaqahcode.muslimlauncher.data.wallpaper.SystemWallpaperState
import com.sadaqahcode.muslimlauncher.domain.model.ColorSource
import com.sadaqahcode.muslimlauncher.domain.model.DarkMode
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.navigation.AppNavHost
import com.sadaqahcode.muslimlauncher.ui.theme.MuslimLauncherTheme
import com.sadaqahcode.muslimlauncher.ui.theme.NightColorScheme
import com.sadaqahcode.muslimlauncher.ui.theme.ParchmentColorScheme
import com.sadaqahcode.muslimlauncher.ui.theme.rememberColorSchemeFromSeed
import com.sadaqahcode.muslimlauncher.ui.widgets.LauncherAppWidgetHost
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import com.sadaqahcode.muslimlauncher.util.applyTransparentSystemBars

class MainActivity : ComponentActivity() {

    companion object {
        private const val APPWIDGET_HOST_ID = 0x1001
        private const val WALLPAPER_DEBOUNCE_MS = 400L

        @Volatile
        lateinit var appWidgetHost: AppWidgetHost
            private set

        @Volatile
        lateinit var appWidgetManager: AppWidgetManager
            private set
    }

    private val _homePressCount = MutableStateFlow(0L)
    val homePressCount: StateFlow<Long> = _homePressCount.asStateFlow()

    private val _wallpaperVersion = MutableStateFlow(0L)
    val wallpaperVersion: StateFlow<Long> = _wallpaperVersion.asStateFlow()

    private var wallpaperDebounceJob: Job? = null

    private val wallpaperColorsListener: WallpaperManager.OnColorsChangedListener? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            WallpaperManager.OnColorsChangedListener { colors, which ->
                if (which and WallpaperManager.FLAG_SYSTEM != 0) {
                    SystemWallpaperState.update(colors)
                }
                debugPrint.d("MainActivity: wallpaper colors changed, scheduling debounce")
                wallpaperDebounceJob?.cancel()
                wallpaperDebounceJob = lifecycleScope.launch {
                    delay(WALLPAPER_DEBOUNCE_MS)
                    debugPrint.d("MainActivity: debounce elapsed, bumping version")
                    _wallpaperVersion.update { it + 1 }
                }
            }
        } else null

    private fun isSystemDark(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyTransparentSystemBars(darkTheme = isSystemDark())
        window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        appWidgetHost = LauncherAppWidgetHost(applicationContext, APPWIDGET_HOST_ID)
        debugPrint.d("MainActivity: host initialized id=$APPWIDGET_HOST_ID")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            wallpaperColorsListener?.let {
                WallpaperManager.getInstance(applicationContext)
                    .addOnColorsChangedListener(it, Handler(Looper.getMainLooper()))
            }
            SystemWallpaperState.tryInit(applicationContext)
        }

        setContent {
            val context = LocalContext.current
            val view = LocalView.current
            val ds = remember { SettingsDataStore(context) }
            val settings by ds.settings.collectAsState(initial = UserSettings())

            val currentWallpaperVersion by wallpaperVersion.collectAsState()

            val darkTheme = when (settings.darkMode) {
                DarkMode.Auto -> isSystemInDarkTheme()
                DarkMode.Light -> false
                DarkMode.Dark -> true
            }

            val colorScheme: ColorScheme = when (settings.colorSource) {

                ColorSource.LauncherDefault -> {
                    if (darkTheme) NightColorScheme else ParchmentColorScheme
                }

                ColorSource.AdaptivePhone -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        remember(currentWallpaperVersion, darkTheme) {
                            if (darkTheme) dynamicDarkColorScheme(context)
                            else dynamicLightColorScheme(context)
                        }
                    } else {
                        if (darkTheme) NightColorScheme else ParchmentColorScheme
                    }
                }

                ColorSource.CustomColor -> {
                    val seed = remember(settings.customSeedColor) {
                        runCatching {
                            android.graphics.Color.parseColor(settings.customSeedColor)
                        }.getOrNull()
                    }
                    if (seed != null) {
                        rememberColorSchemeFromSeed(seed, darkTheme)
                    } else {
                        if (darkTheme) NightColorScheme else ParchmentColorScheme
                    }
                }
            }

            // Update icons again when settings.darkMode changes, helper already set the rest in onCreate
            SideEffect {
                if (!view.isInEditMode) {
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
            }

            MuslimLauncherTheme(
                colorScheme = colorScheme,
                darkTheme = darkTheme,
            ) {
                AppNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == Intent.ACTION_MAIN &&
            intent.hasCategory(Intent.CATEGORY_HOME)
        ) {
            debugPrint.d("MainActivity: HOME intent received, bumping counter")
            _homePressCount.update { it + 1 }
        }
    }

    override fun onStart() {
        super.onStart()
        runCatching { appWidgetHost.startListening() }
            .onFailure { debugPrint.e("onStart: startListening failed: ${it.message}") }
        debugPrint.d("MainActivity: host listening")
    }

    override fun onStop() {
        super.onStop()
        runCatching { appWidgetHost.stopListening() }
            .onFailure { debugPrint.e("onStop: stopListening failed: ${it.message}") }
        debugPrint.d("MainActivity: host stopped")
    }

    override fun onDestroy() {
        wallpaperDebounceJob?.cancel()
        wallpaperDebounceJob = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            wallpaperColorsListener?.let {
                WallpaperManager.getInstance(applicationContext)
                    .removeOnColorsChangedListener(it)
            }
        }
        super.onDestroy()
    }
}
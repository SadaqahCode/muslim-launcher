package com.sadaqahcode.muslimlauncher.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.data.local.store.SettingsDataStore
import com.sadaqahcode.muslimlauncher.domain.model.AppIconShape
import com.sadaqahcode.muslimlauncher.domain.model.AppIconSize
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.AppLanguage
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.ColorSource
import com.sadaqahcode.muslimlauncher.domain.model.DarkMode
import com.sadaqahcode.muslimlauncher.domain.model.DrawerLayout
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application,
    private val ds: SettingsDataStore,
) : AndroidViewModel(app) {

    val settings: StateFlow<UserSettings> = ds.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val _wallpaperApplyCount = MutableStateFlow(0)

    val wallpaperApplying: StateFlow<Boolean> = _wallpaperApplyCount
        .map { it > 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // Settings setters
    fun setIconSize(v: AppIconSize) = viewModelScope.launch { ds.setIconSize(v) }
    fun setTextSize(v: AppTextSize) = viewModelScope.launch { ds.setTextSize(v) }
    fun setItemLayout(v: AppItemLayout) = viewModelScope.launch { ds.setItemLayout(v) }
    fun setIconShape(v: AppIconShape) = viewModelScope.launch { ds.setIconShape(v) }
    fun setDrawerLayout(v: DrawerLayout) = viewModelScope.launch { ds.setDrawerLayout(v) }
    /*
    // we used auto select for mvp
    fun setHomeColumns(v: Int) = viewModelScope.launch { ds.setHomeColumns(v) }
    fun setDrawerColumns(v: Int) = viewModelScope.launch { ds.setDrawerColumns(v) }
    fun setDockSlots(v: Int) = viewModelScope.launch { ds.setDockSlots(v) }
    fun setMaxPinned(v: Int) = viewModelScope.launch { ds.setMaxPinned(v) }
    */

    fun setPaddingTopEnabled(v: Boolean) {
        viewModelScope.launch { ds.setPaddingTopEnabled(v) }
    }

    fun setDarkMode(v: DarkMode) = viewModelScope.launch { ds.setDarkMode(v) }

    // Color source
    fun setColorSource(v: ColorSource) = viewModelScope.launch {
        ds.setColorSource(v)
    }

    fun setCustomSeedColor(hex: String) = viewModelScope.launch {
        ds.setCustomSeedColor(hex)
    }

    fun setWallpaperTextMode(v: WallpaperTextMode) = viewModelScope.launch {
        ds.setWallpaperTextMode(v)
    }

    fun setWallpaperMode(v: WallpaperMode, schemeBackground: String) = viewModelScope.launch {
        runWallpaperApply {
            if (v == WallpaperMode.System) {
                ds.setWallpaperColor(schemeBackground)
            }
            ds.setWallpaperMode(v)
            applyToSystemWallpaper(
                getApplication(),
                settings.value.copy(
                    wallpaperMode = v,
                    wallpaperColor = if (v == WallpaperMode.System) schemeBackground else settings.value.wallpaperColor
                ),
            )
        }
    }

    fun setWallpaperColor(hex: String) = viewModelScope.launch {
        runWallpaperApply {
            ds.setWallpaperColor(hex)
            applyToSystemWallpaper(
                getApplication(),
                settings.value.copy(wallpaperColor = hex),
            )
        }
    }

    fun setWallpaperGradient(start: String, end: String) = viewModelScope.launch {
        runWallpaperApply {
            ds.setWallpaperGradient(start, end)
            applyToSystemWallpaper(
                getApplication(),
                settings.value.copy(
                    wallpaperGradientStart = start,
                    wallpaperGradientEnd = end,
                ),
            )
        }
    }

    fun setWallpaperImageUri(uri: String?) = viewModelScope.launch {
        ds.setWallpaperImageUri(uri)
        if (uri != null) {
            runWallpaperApply {
                applyToSystemWallpaper(
                    getApplication(),
                    settings.value.copy(
                        wallpaperImageUri = uri,
                        wallpaperMode = WallpaperMode.Image,
                    ),
                )
            }
        }
    }

    fun setLanguage(v: AppLanguage) = viewModelScope.launch { ds.setLanguage(v) }

    fun setWallpaperBlurEnabled(v: Boolean) = viewModelScope.launch {
        ds.setWallpaperBlurEnabled(v)
    }

    fun setWallpaperBlurRadius(v: Int) = viewModelScope.launch {
        ds.setWallpaperBlurRadius(v)
    }

    private suspend fun runWallpaperApply(block: suspend () -> Unit) {
        _wallpaperApplyCount.update { it + 1 }
        try {
            block()
        } finally {
            _wallpaperApplyCount.update { it - 1 }
        }
    }
}
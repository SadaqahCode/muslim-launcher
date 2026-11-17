package com.sadaqahcode.muslimlauncher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sadaqahcode.muslimlauncher.domain.model.AppIconShape
import com.sadaqahcode.muslimlauncher.domain.model.AppIconSize
import com.sadaqahcode.muslimlauncher.domain.model.AppItemLayout
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.domain.model.ColorSource
import com.sadaqahcode.muslimlauncher.domain.model.DarkMode
import com.sadaqahcode.muslimlauncher.domain.model.DrawerLayout
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperMode
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userSettingsDataStore: DataStore<Preferences> by
preferencesDataStore("user_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val iconSize = stringPreferencesKey("icon_size")
        val textSize = stringPreferencesKey("text_size")
        val itemLayout = stringPreferencesKey("item_layout")
        val iconShape = stringPreferencesKey("icon_shape")
        val drawerLayout = stringPreferencesKey("drawer_layout")
        val homeColumns = intPreferencesKey("home_columns")
        val drawerColumns = intPreferencesKey("drawer_columns")
        val dockSlots = intPreferencesKey("dock_slots")
        val darkMode = stringPreferencesKey("dark_mode")
        val maxPinned = intPreferencesKey("max_pinned")

        // Color source
        val colorSource = stringPreferencesKey("color_source")
        val customSeedColor = stringPreferencesKey("custom_seed_color")

        // Wallpaper
        val wallpaperMode = stringPreferencesKey("wallpaper_mode")
        val wallpaperColor = stringPreferencesKey("wallpaper_color")
        val wallpaperGradientStart = stringPreferencesKey("wallpaper_gradient_start")
        val wallpaperGradientEnd = stringPreferencesKey("wallpaper_gradient_end")
        val wallpaperImageUri = stringPreferencesKey("wallpaper_image_uri")
        val wallpaperBlurEnabled = booleanPreferencesKey("wallpaper_blur_enabled")
        val wallpaperBlurRadius = intPreferencesKey("wallpaper_blur_radius")
        val wallpaperTextMode = stringPreferencesKey("wallpaper_text_mode")
    }

    val settings: Flow<UserSettings> = context.userSettingsDataStore.data.map { p ->
        UserSettings(
            iconSize = p.enumOr(Keys.iconSize, AppIconSize.Medium),
            textSize = p.enumOr(Keys.textSize, AppTextSize.Medium),
            itemLayout = p.enumOr(Keys.itemLayout, AppItemLayout.TextBelow),
            iconShape = p.enumOr(Keys.iconShape, AppIconShape.Rounded),
            drawerLayout = p.enumOr(Keys.drawerLayout, DrawerLayout.Grid),
            homeColumns = (p[Keys.homeColumns] ?: 4).coerceIn(2, 5),
            drawerColumns = (p[Keys.drawerColumns] ?: 4).coerceIn(3, 6),
            dockSlots = (p[Keys.dockSlots] ?: 4).coerceIn(3, 8),
            maxPinned = (p[Keys.maxPinned] ?: 8).coerceIn(4, 24),
            darkMode = p.enumOr(Keys.darkMode, DarkMode.Auto),

            colorSource = p.enumOr(Keys.colorSource, ColorSource.LauncherDefault),
            customSeedColor = p[Keys.customSeedColor] ?: "#FFFF8F00",

            wallpaperMode = p.enumOr(Keys.wallpaperMode, WallpaperMode.Gradient),
            wallpaperColor = p[Keys.wallpaperColor] ?: "#FFF5F1EA",
            wallpaperGradientStart = p[Keys.wallpaperGradientStart] ?: "#FF0A1428",
            wallpaperGradientEnd = p[Keys.wallpaperGradientEnd] ?: "#FF1F5FA8",
            wallpaperImageUri = p[Keys.wallpaperImageUri]?.takeIf { it.isNotBlank() },
            wallpaperBlurEnabled = p[Keys.wallpaperBlurEnabled] ?: false,
            wallpaperBlurRadius = (p[Keys.wallpaperBlurRadius] ?: 20).coerceIn(1, 50),
            wallpaperTextMode = p.enumOr(Keys.wallpaperTextMode, WallpaperTextMode.Auto),
        )
    }

    suspend fun setIconSize(v: AppIconSize) = write { it[Keys.iconSize] = v.name }
    suspend fun setTextSize(v: AppTextSize) = write { it[Keys.textSize] = v.name }
    suspend fun setItemLayout(v: AppItemLayout) = write { it[Keys.itemLayout] = v.name }
    suspend fun setIconShape(v: AppIconShape) = write { it[Keys.iconShape] = v.name }
    suspend fun setDrawerLayout(v: DrawerLayout) = write { it[Keys.drawerLayout] = v.name }
    suspend fun setHomeColumns(v: Int) = write { it[Keys.homeColumns] = v.coerceIn(2, 5) }
    suspend fun setDrawerColumns(v: Int) = write { it[Keys.drawerColumns] = v.coerceIn(3, 6) }
    suspend fun setDockSlots(v: Int) = write { it[Keys.dockSlots] = v.coerceIn(3, 8) }
    suspend fun setDarkMode(v: DarkMode) = write { it[Keys.darkMode] = v.name }
    suspend fun setMaxPinned(v: Int) = write { it[Keys.maxPinned] = v.coerceIn(4, 24) }

    suspend fun setColorSource(v: ColorSource) =
        write { it[Keys.colorSource] = v.name }
    suspend fun setCustomSeedColor(v: String) =
        write { it[Keys.customSeedColor] = v }

    suspend fun setWallpaperMode(v: WallpaperMode) =
        write { it[Keys.wallpaperMode] = v.name }
    suspend fun setWallpaperColor(v: String) =
        write { it[Keys.wallpaperColor] = v }
    suspend fun setWallpaperGradient(start: String, end: String) = write {
        it[Keys.wallpaperGradientStart] = start
        it[Keys.wallpaperGradientEnd] = end
    }
    suspend fun setWallpaperImageUri(v: String?) =
        write { it[Keys.wallpaperImageUri] = v ?: "" }
    suspend fun setWallpaperBlurEnabled(v: Boolean) =
        write { it[Keys.wallpaperBlurEnabled] = v }
    suspend fun setWallpaperBlurRadius(v: Int) =
        write { it[Keys.wallpaperBlurRadius] = v.coerceIn(1, 50) }
    suspend fun setWallpaperTextMode(v: WallpaperTextMode) =
        write { it[Keys.wallpaperTextMode] = v.name }

    private suspend fun write(block: (MutablePreferences) -> Unit) {
        context.userSettingsDataStore.edit { block(it) }
    }

    private inline fun <reified E : Enum<E>> Preferences.enumOr(
        key: Preferences.Key<String>,
        default: E,
    ): E = this[key]?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default
}
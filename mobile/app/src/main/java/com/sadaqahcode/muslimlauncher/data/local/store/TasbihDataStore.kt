package com.sadaqahcode.muslimlauncher.data.local.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sadaqahcode.muslimlauncher.di.TasbihDataStore
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.HapticMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbihDataStore @Inject constructor(
    @TasbihDataStore private val dataStore: DataStore<Preferences>,
) {
    data class Saved(
        val freeKey: String?,
        val freeTarget: Int?,
        val lastMode: String?,
        val lastGuidedKey: String?,
    )

    private object Keys {
        val FavoriteFree = stringSetPreferencesKey("favorite_free")
        val FavoriteGuided = stringSetPreferencesKey("favorite_guided")
        val LastFree = stringPreferencesKey("last_free_key")
        val LastMode = stringPreferencesKey("last_mode") // "Free" / "Guided"
        val LastGuided = stringPreferencesKey("last_guided_key")
        val FreeTarget = intPreferencesKey("free_target")
        val HapticMode = stringPreferencesKey("haptic_mode")
        val OnboardingDone = booleanPreferencesKey("onboarding_done")
    }

    private val prefsFlow: Flow<Preferences> = dataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }

    val favoriteFree: Flow<Set<String>> =
        prefsFlow.map { it[Keys.FavoriteFree] ?: emptySet() }.distinctUntilChanged()

    val favoriteGuided: Flow<Set<String>> =
        prefsFlow.map { it[Keys.FavoriteGuided] ?: emptySet() }.distinctUntilChanged()

    val hapticMode: Flow<HapticMode> = prefsFlow
        .map { p ->
            p[Keys.HapticMode]?.let { raw ->
                runCatching { HapticMode.valueOf(raw) }.getOrNull()
            } ?: HapticMode.Vibration
        }
        .distinctUntilChanged()

    // ── Onboarding ────────────────────────────────────────────────────────
    val onboardingDone: Flow<Boolean> =
        prefsFlow.map { it[Keys.OnboardingDone] ?: false }.distinctUntilChanged()

    // ── Reads ─────────────────────────────────────────────────────────────

    suspend fun readSaved(): Saved {
        val p = prefsFlow.first()
        return Saved(
            freeKey = p[Keys.LastFree],
            freeTarget = p[Keys.FreeTarget],
            lastMode = p[Keys.LastMode],
            lastGuidedKey = p[Keys.LastGuided],
        )
    }

    // ── Writes ────────────────────────────────────────────────────────────

    suspend fun toggleFavoriteFree(key: String) = toggle(Keys.FavoriteFree, key)

    suspend fun toggleFavoriteGuided(key: String) = toggle(Keys.FavoriteGuided, key)

    suspend fun saveLastFree(key: String) {
        dataStore.edit { it[Keys.LastFree] = key }
    }

    suspend fun saveLastMode(mode: String) {
        dataStore.edit { it[Keys.LastMode] = mode }
    }

    suspend fun saveLastGuided(key: String) {
        dataStore.edit { it[Keys.LastGuided] = key }
    }

    suspend fun saveFreeTarget(target: Int) {
        dataStore.edit { it[Keys.FreeTarget] = target }
    }

    suspend fun saveHapticMode(mode: HapticMode) {
        dataStore.edit { it[Keys.HapticMode] = mode.name }
    }

    suspend fun markOnboardingDone() {
        dataStore.edit { it[Keys.OnboardingDone] = true }
    }

    suspend fun resetOnboarding() {
        dataStore.edit { it.remove(Keys.OnboardingDone) }
    }

    private suspend fun toggle(key: Preferences.Key<Set<String>>, value: String) {
        dataStore.edit { p ->
            val current = p[key] ?: emptySet()
            p[key] = if (value in current) current - value else current + value
        }
    }
}
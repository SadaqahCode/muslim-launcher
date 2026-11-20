package com.sadaqahcode.muslimlauncher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrCacheEntity
import com.sadaqahcode.muslimlauncher.data.local.mapper.DhikrCacheMapper
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dhikrDataStore: DataStore<Preferences> by preferencesDataStore("dhikr_cache")

@Singleton
class DhikrLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val cacheMapper: DhikrCacheMapper,
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("dhikr_cache")
        private val LAST_UPDATED_KEY = longPreferencesKey("dhikr_cache_last_updated")
    }

    fun getCachedContent(): Flow<List<DhikrItem>?> =
        context.dhikrDataStore.data.map { prefs ->
            prefs[CACHE_KEY]?.let { raw ->
                try {
                    cacheMapper.toDomain(json.decodeFromString<DhikrCacheEntity>(raw))
                } catch (e: Exception) {
                    null
                }
            }
        }

    suspend fun saveCache(items: List<DhikrItem>) {
        val entity = cacheMapper.toEntity(items)
        val raw = json.encodeToString(DhikrCacheEntity.serializer(), entity)
        context.dhikrDataStore.edit { prefs ->
            prefs[CACHE_KEY] = raw
            prefs[LAST_UPDATED_KEY] = entity.lastUpdated
        }
    }

    suspend fun clearCache() {
        context.dhikrDataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
            prefs.remove(LAST_UPDATED_KEY)
        }
    }

    suspend fun getLastUpdated(): Long? =
        context.dhikrDataStore.data.map { it[LAST_UPDATED_KEY] }.first()

    suspend fun getCacheAgeSeconds(): Long? {
        val lastUpdated = getLastUpdated() ?: return null
        return (System.currentTimeMillis() - lastUpdated) / 1000
    }

    suspend fun isCacheValid(maxAgeSeconds: Long): Boolean {
        if (maxAgeSeconds < 0) return getLastUpdated() != null

        val lastUpdated = getLastUpdated() ?: return false
        if (maxAgeSeconds == 0L) return false

        val ageSeconds = (System.currentTimeMillis() - lastUpdated) / 1000
        return ageSeconds < maxAgeSeconds
    }
}
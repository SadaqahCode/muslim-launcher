package com.sadaqahcode.muslimlauncher.data.local.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrCacheEntity
import com.sadaqahcode.muslimlauncher.data.local.mapper.DhikrCacheMapper
import com.sadaqahcode.muslimlauncher.di.DhikrDataStore
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.DhikrItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DhikrLocalDataSource @Inject constructor(
    @DhikrDataStore private val dataStore: DataStore<Preferences>,
    private val json: Json,
    private val cacheMapper: DhikrCacheMapper,
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("dhikr_cache")
        private val LAST_UPDATED_KEY = longPreferencesKey("dhikr_cache_last_updated")
    }

    fun getCachedContent(): Flow<List<DhikrItem>?> =
        dataStore.data.map { prefs ->
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
        dataStore.edit { prefs ->
            prefs[CACHE_KEY] = raw
            prefs[LAST_UPDATED_KEY] = entity.lastUpdated
        }
    }

    suspend fun clearCache() {
        dataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
            prefs.remove(LAST_UPDATED_KEY)
        }
    }

    suspend fun getLastUpdated(): Long? =
        dataStore.data.map { it[LAST_UPDATED_KEY] }.first()

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
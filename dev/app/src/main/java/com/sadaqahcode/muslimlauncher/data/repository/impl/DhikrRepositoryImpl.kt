package com.sadaqahcode.muslimlauncher.data.repository

import com.flagodna.debugprint.dlog
import com.sadaqahcode.muslimlauncher.data.local.DhikrLocalDataSource
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiService
import com.sadaqahcode.muslimlauncher.data.remote.mapper.DhikrDtoMapper
import com.sadaqahcode.muslimlauncher.di.IoDispatcher
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.repository.DhikrRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DhikrRepositoryImpl @Inject constructor(
    private val localDataSource: DhikrLocalDataSource,
    private val apiService: ApiService,
    private val dtoMapper: DhikrDtoMapper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : DhikrRepository {

    companion object {
        private const val TAG = "DhikrRepo"
        private const val ERROR_NETWORK_NO_CACHE =
            "Failed to connect to the network & no dhikr data is available."
        private const val ERROR_NO_CACHE =
            "No dhikr data is available."
        private const val ERROR_REFRESH_FAILED =
            "Failed to load dhikr data & no cache is available."
    }

    override fun getDhikrItems(
        tryOnlineFirst: Boolean,
        cacheValiditySeconds: Long,
    ): Flow<Result<List<DhikrItem>>> = flow {
        dlog.d(TAG, "getDhikrItems() called, tryOnlineFirst=$tryOnlineFirst")

        if (tryOnlineFirst) {
            // ── Online-first: block on network, fall back to cache on failure ──
            dlog.d(TAG, "🔄 Online-first mode: fetching from network...")
            emit(Result.Loading)
            try {
                val freshData = fetchFromNetwork()
                dlog.i(TAG, "Network fetch SUCCESS, items: ${freshData.size}")
                localDataSource.saveCache(freshData)
                dlog.d(TAG, "💾 Cache updated with fresh data")
                emit(Result.Success(freshData))
            } catch (e: Exception) {
                dlog.e(TAG, "Network fetch FAILED: ${e.message}")

                val cachedData = loadFromCache()
                if (cachedData != null && cachedData.isNotEmpty()) {
                    dlog.i(TAG, "📦 Falling back to cache, items: ${cachedData.size}")
                    emit(Result.Success(cachedData))
                } else {
                    dlog.e(TAG, "❌ No cache available, emitting error")
                    emit(Result.Error(e, ERROR_NETWORK_NO_CACHE))
                }
            }
        } else {
            // ── Cache-first: never block on network if we have fresh cache ──
            dlog.d(TAG, "📦 Cache-first mode: checking cache...")

            val isValid = localDataSource.isCacheValid(cacheValiditySeconds)
            val cacheAge = localDataSource.getCacheAgeSeconds()

            dlog.d(
                TAG,
                "📦 Cache age: ${cacheAge ?: "N/A"}s, valid: $isValid (threshold: ${cacheValiditySeconds}s)"
            )

            // 1. Valid + non-empty cache → emit and STOP. No network refresh.
            if (isValid) {
                var emittedFromValidCache = false
                localDataSource.getCachedContent().map { cachedItems ->
                    if (cachedItems != null && cachedItems.isNotEmpty()) {
                        dlog.i(TAG, "✅ Valid cache hit! Emitting ${cachedItems.size} items")
                        emit(Result.Success(cachedItems))
                        emittedFromValidCache = true
                    } else {
                        dlog.w(TAG, "⚠️ Cache marked valid but empty — will fetch from network")
                    }
                }.first()

                if (emittedFromValidCache) {
                    dlog.d(TAG, "🛑 Valid cache hit, skipping network refresh")
                    return@flow
                }
                // else: fall through — a "valid but empty" cache is treated as no cache
            } else {
                if (cacheAge != null) {
                    dlog.w(TAG, "⚠️ Cache expired! Age: ${cacheAge}s")
                } else {
                    dlog.w(TAG, "⚠️ No cache found")
                }
            }

            // 2. Cache is stale or missing.
            //    Emit whatever stale data we have so the UI has something to show,
            //    otherwise emit Loading so a spinner can appear.
            val staleCache = loadFromCache()
            if (staleCache != null && staleCache.isNotEmpty()) {
                dlog.i(TAG, "📦 Emitting expired cache while fetching fresh...")
                emit(Result.Success(staleCache))
            } else {
                dlog.d(TAG, "⏳ No cached data to show, emitting Loading")
                emit(Result.Loading)
            }

            // 3. Refresh in the background. On success, emit fresh data.
            //    On failure, keep the stale cache (already emitted) or surface the error
            //    if we had nothing to show.
            dlog.d(TAG, "🌐 Fetching fresh data from network in background...")
            try {
                val freshData = fetchFromNetwork()
                dlog.i(TAG, "✅ Network fetch SUCCESS, items: ${freshData.size}")
                localDataSource.saveCache(freshData)
                dlog.d(TAG, "💾 Cache updated with fresh data")
                emit(Result.Success(freshData))
            } catch (e: Exception) {
                dlog.e(TAG, "❌ Network fetch FAILED: ${e.message}")

                if (staleCache != null && staleCache.isNotEmpty()) {
                    dlog.i(TAG, "📦 Keeping existing cache (network failed)")
                    // No extra emission — stale data is already on screen.
                } else {
                    dlog.e(TAG, "❌ No cache available to fallback to")
                    emit(Result.Error(e, ERROR_NETWORK_NO_CACHE))
                }
            }
        }
    }.flowOn(dispatcher)

    override suspend fun refreshDhikrItems(): Result<List<DhikrItem>> = withContext(dispatcher) {
        dlog.d(TAG, "🔄 refreshDhikrItems() called")

        // Fetch first, save only on success. This way a failed refresh
        // (no internet, server error) never destroys the existing cache.
        return@withContext try {
            val freshData = fetchFromNetwork()
            dlog.i(TAG, "✅ Fresh data fetched, items: ${freshData.size}")
            localDataSource.saveCache(freshData)
            dlog.d(TAG, "💾 Fresh data saved to cache")
            Result.Success(freshData)
        } catch (e: Exception) {
            dlog.e(TAG, "❌ Refresh failed: ${e.message}")

            val cachedData = loadFromCache()
            if (cachedData != null && cachedData.isNotEmpty()) {
                dlog.i(TAG, "📦 Using cached data as fallback, items: ${cachedData.size}")
                Result.Success(cachedData)
            } else {
                dlog.e(TAG, "❌ No cache to fall back to")
                Result.Error(e, ERROR_REFRESH_FAILED)
            }
        }
    }

    override suspend fun getCachedOnly(): Result<List<DhikrItem>> = withContext(dispatcher) {
        val cached = loadFromCache()
        if (cached != null) {
            Result.Success(cached)
        } else {
            Result.Error(IllegalStateException(ERROR_NO_CACHE))
        }
    }

    override suspend fun clearCache() = withContext(dispatcher) {
        localDataSource.clearCache()
        dlog.i(TAG, "Cache cleared")
    }

    private suspend fun fetchFromNetwork(): List<DhikrItem> {
        dlog.d(TAG, "🌐 Fetching from API...")
        val startTime = System.currentTimeMillis()

        return try {
            val result = dtoMapper.toDomain(apiService.getDhikrItems())
            val duration = System.currentTimeMillis() - startTime
            dlog.d(TAG, "🌐 API response received in ${duration}ms, items: ${result.size}")
            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            dlog.e(TAG, "🌐 API call failed after ${duration}ms: ${e.message}")
            throw e
        }
    }

    private suspend fun loadFromCache(): List<DhikrItem>? {
        dlog.d(TAG, "📦 Loading from cache...")
        val startTime = System.currentTimeMillis()

        return try {
            val result = localDataSource.getCachedContent().first()
            val duration = System.currentTimeMillis() - startTime
            if (result != null) {
                dlog.d(TAG, "📦 Cache loaded in ${duration}ms, items: ${result.size}")
            } else {
                dlog.w(TAG, "📦 Cache is empty")
            }
            result
        } catch (e: Exception) {
            dlog.e(TAG, "📦 Cache load failed: ${e.message}")
            null
        }
    }
}
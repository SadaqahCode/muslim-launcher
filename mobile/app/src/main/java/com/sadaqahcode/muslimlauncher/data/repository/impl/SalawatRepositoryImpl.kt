package com.sadaqahcode.muslimlauncher.data.repository.impl

import com.flagodna.debugprint.dlog
import com.sadaqahcode.muslimlauncher.data.local.source.SalawatLocalDataSource
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiService
import com.sadaqahcode.muslimlauncher.data.remote.mapper.SalawatDtoMapper
import com.sadaqahcode.muslimlauncher.di.IoDispatcher
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import com.sadaqahcode.muslimlauncher.domain.repository.SalawatRepository
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
class SalawatRepositoryImpl @Inject constructor(
    private val localDataSource: SalawatLocalDataSource,
    private val apiService: ApiService,
    private val dtoMapper: SalawatDtoMapper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : SalawatRepository {

    private companion object {
        const val TAG = "SalawatRepo"
        const val ERROR_NETWORK_NO_CACHE =
            "Failed to connect to the network & no salawat data is available."
        const val ERROR_NO_CACHE =
            "No salawat data is available."
        const val ERROR_REFRESH_FAILED =
            "Failed to load salawat data & no cache is available."
    }

    override fun getSalawat(
        tryOnlineFirst: Boolean,
        cacheValiditySeconds: Long,
    ): Flow<Result<List<Salawat>>> = flow {
        dlog.d(TAG, "getSalawat() called, tryOnlineFirst=$tryOnlineFirst")

        if (tryOnlineFirst) {
            dlog.d(TAG, "🔄 Online-first mode")
            emit(Result.Loading)
            try {
                val fresh = fetchFromNetwork()
                dlog.i(TAG, "Network SUCCESS, items: ${fresh.size}")
                localDataSource.saveCache(fresh)
                emit(Result.Success(fresh))
            } catch (e: Exception) {
                dlog.e(TAG, "Network FAILED: ${e.message}")
                val cached = loadFromCache()
                if (cached != null && cached.isNotEmpty()) {
                    dlog.i(TAG, "📦 Fallback to cache, items: ${cached.size}")
                    emit(Result.Success(cached))
                } else {
                    emit(Result.Error(e, ERROR_NETWORK_NO_CACHE))
                }
            }
            return@flow
        }

        // Cache-first
        val isValid = localDataSource.isCacheValid(cacheValiditySeconds)
        val cacheAge = localDataSource.getCacheAgeSeconds()
        dlog.d(TAG, "📦 age=${cacheAge ?: "N/A"}s valid=$isValid")

        if (isValid) {
            var emittedValid = false
            localDataSource.getCachedContent().map { cachedItems ->
                if (!cachedItems.isNullOrEmpty()) {
                    emit(Result.Success(cachedItems))
                    emittedValid = true
                }
            }.first()
            if (emittedValid) {
                dlog.d(TAG, "🛑 Valid cache hit, skip network")
                return@flow
            }
        }

        // Stale or missing
        val staleCache = loadFromCache()
        if (!staleCache.isNullOrEmpty()) {
            emit(Result.Success(staleCache))
        } else {
            emit(Result.Loading)
        }

        try {
            val fresh = fetchFromNetwork()
            dlog.i(TAG, "✅ Network SUCCESS, items: ${fresh.size}")
            localDataSource.saveCache(fresh)
            emit(Result.Success(fresh))
        } catch (e: Exception) {
            dlog.e(TAG, "❌ Network FAILED: ${e.message}")
            if (staleCache.isNullOrEmpty()) {
                emit(Result.Error(e, ERROR_NETWORK_NO_CACHE))
            }
        }
    }.flowOn(dispatcher)

    override suspend fun refreshSalawat(): Result<List<Salawat>> = withContext(dispatcher) {
        return@withContext try {
            val fresh = fetchFromNetwork()
            localDataSource.saveCache(fresh)
            Result.Success(fresh)
        } catch (e: Exception) {
            val cached = loadFromCache()
            if (!cached.isNullOrEmpty()) {
                Result.Success(cached)
            } else {
                Result.Error(e, ERROR_REFRESH_FAILED)
            }
        }
    }

    override suspend fun getCachedOnly(): Result<List<Salawat>> = withContext(dispatcher) {
        val cached = loadFromCache()
        if (cached != null) Result.Success(cached)
        else Result.Error(IllegalStateException(ERROR_NO_CACHE))
    }

    override suspend fun clearCache() = withContext(dispatcher) {
        localDataSource.clearCache()
        dlog.i(TAG, "Cache cleared")
    }

    private suspend fun fetchFromNetwork(): List<Salawat> {
        val start = System.currentTimeMillis()
        return try {
            val response = apiService.getSalawat()
            val result = dtoMapper.toDomain(response)
            dlog.d(TAG, "API ${System.currentTimeMillis() - start}ms, items: ${result.size}")
            result
        } catch (e: Exception) {
            dlog.e(TAG, "API failed ${System.currentTimeMillis() - start}ms: ${e.message}")
            throw e
        }
    }

    private suspend fun loadFromCache(): List<Salawat>? = try {
        localDataSource.getCachedContent().first()
    } catch (e: Exception) {
        dlog.e(TAG, "cache read failed: ${e.message}")
        null
    }
}
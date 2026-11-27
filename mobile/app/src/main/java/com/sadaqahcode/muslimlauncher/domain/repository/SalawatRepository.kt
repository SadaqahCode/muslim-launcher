package com.sadaqahcode.muslimlauncher.domain.repository

import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import kotlinx.coroutines.flow.Flow

interface SalawatRepository {

    fun getSalawat(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = DEFAULT_CACHE_VALIDITY_SECONDS,
    ): Flow<Result<List<Salawat>>>

    suspend fun refreshSalawat(): Result<List<Salawat>>

    suspend fun getCachedOnly(): Result<List<Salawat>>

    suspend fun clearCache()

    companion object {
        const val DEFAULT_CACHE_VALIDITY_SECONDS = 3_600L
    }
}
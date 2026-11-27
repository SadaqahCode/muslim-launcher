package com.sadaqahcode.muslimlauncher.domain.usecase.tools

import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import com.sadaqahcode.muslimlauncher.domain.repository.SalawatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSalawatUseCase @Inject constructor(
    private val repository: SalawatRepository,
) {
    operator fun invoke(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = SalawatRepository.DEFAULT_CACHE_VALIDITY_SECONDS,
    ): Flow<Result<List<Salawat>>> =
        repository.getSalawat(tryOnlineFirst, cacheValiditySeconds)
}
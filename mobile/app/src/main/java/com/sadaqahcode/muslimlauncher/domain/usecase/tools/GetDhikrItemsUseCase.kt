package com.sadaqahcode.muslimlauncher.domain.usecase.tools

import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.repository.DhikrRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDhikrItemsUseCase @Inject constructor(
    private val repository: DhikrRepository,
) {
    operator fun invoke(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = DhikrRepository.DEFAULT_CACHE_VALIDITY_SECONDS,
    ): Flow<Result<List<DhikrItem>>> =
        repository.getDhikrItems(tryOnlineFirst, cacheValiditySeconds)
}
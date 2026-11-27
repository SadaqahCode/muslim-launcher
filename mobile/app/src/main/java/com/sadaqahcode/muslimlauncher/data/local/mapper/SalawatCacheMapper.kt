package com.sadaqahcode.muslimlauncher.data.local.mapper

import com.sadaqahcode.muslimlauncher.data.local.entity.SalawatCacheEntity
import com.sadaqahcode.muslimlauncher.data.local.entity.SalawatCacheItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import javax.inject.Inject

class SalawatCacheMapper @Inject constructor() {

    fun toEntity(
        items: List<Salawat>,
        lastUpdated: Long = System.currentTimeMillis(),
    ): SalawatCacheEntity = SalawatCacheEntity(
        items = items.map { it.toCache() },
        lastUpdated = lastUpdated,
    )

    fun toDomain(entity: SalawatCacheEntity): List<Salawat> =
        entity.items.map { it.toDomain() }

    private fun Salawat.toCache(): SalawatCacheItem = SalawatCacheItem(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
        contexts = contexts,
    )

    private fun SalawatCacheItem.toDomain(): Salawat = Salawat(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
        contexts = contexts,
    )
}
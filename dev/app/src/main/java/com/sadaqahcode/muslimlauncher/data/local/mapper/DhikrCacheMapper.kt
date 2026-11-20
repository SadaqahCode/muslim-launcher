package com.sadaqahcode.muslimlauncher.data.local.mapper

import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrCacheEntity
import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrEntryCache
import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrItemCache
import com.sadaqahcode.muslimlauncher.data.local.entity.DhikrPhraseCache
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrEntry
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import javax.inject.Inject

class DhikrCacheMapper @Inject constructor() {

    fun toEntity(items: List<DhikrItem>, lastUpdated: Long = System.currentTimeMillis()): DhikrCacheEntity =
        DhikrCacheEntity(
            items = items.map { it.toCache() },
            lastUpdated = lastUpdated,
        )

    fun toDomain(entity: DhikrCacheEntity): List<DhikrItem> =
        entity.items.map { it.toDomain() }

    private fun DhikrItem.toCache(): DhikrItemCache = DhikrItemCache(
        id = id,
        names = names,
        texts = texts,
        position = position,
        entries = entries.map { it.toCache() },
    )

    private fun DhikrEntry.toCache(): DhikrEntryCache = DhikrEntryCache(
        dhikr = dhikr.toCache(),
        repetitions = repetitions,
    )

    private fun Dhikr.toCache(): DhikrPhraseCache = DhikrPhraseCache(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
    )

    private fun DhikrItemCache.toDomain(): DhikrItem = DhikrItem(
        id = id,
        names = names,
        texts = texts,
        position = position,
        entries = entries.map { it.toDomain() },
    )

    private fun DhikrEntryCache.toDomain(): DhikrEntry = DhikrEntry(
        dhikr = dhikr.toDomain(),
        repetitions = repetitions,
    )

    private fun DhikrPhraseCache.toDomain(): Dhikr = Dhikr(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
    )
}
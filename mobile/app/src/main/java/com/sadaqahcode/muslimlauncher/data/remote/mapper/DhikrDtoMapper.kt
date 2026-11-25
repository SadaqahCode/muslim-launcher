package com.sadaqahcode.muslimlauncher.data.remote.mapper

import com.sadaqahcode.muslimlauncher.data.remote.dto.DhikrDto
import com.sadaqahcode.muslimlauncher.data.remote.dto.DhikrResponseDto
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrEntry
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import javax.inject.Inject

/**
 * Resolves the normalized DTO (flat dhikrs list + dhikr_items referencing
 * them by id) into the denormalized domain shape: each item already carries
 * its full [DhikrEntry] list with the [Dhikr] object embedded.
 */
class DhikrDtoMapper @Inject constructor() {

    fun toDomain(dto: DhikrResponseDto): List<DhikrItem> {
        val byId: Map<Int, Dhikr> = dto.dhikrs.associate { it.id to it.toDomain() }

        return dto.dhikrItems
            .mapNotNull { item ->
                val entries = item.dhikrIds.mapIndexedNotNull { index, dhikrId ->
                    val dhikr = byId[dhikrId] ?: return@mapIndexedNotNull null
                    DhikrEntry(
                        dhikr = dhikr,
                        repetitions = item.repetitions.getOrNull(index) ?: 1,
                    )
                }
                if (entries.isEmpty()) return@mapNotNull null

                DhikrItem(
                    id = item.id,
                    names = item.names,
                    texts = item.texts,
                    position = item.position,
                    entries = entries,
                )
            }
            .sortedBy { it.position }
    }

    private fun DhikrDto.toDomain(): Dhikr = Dhikr(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
    )
}
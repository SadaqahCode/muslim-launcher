package com.sadaqahcode.muslimlauncher.data.remote.mapper

import com.sadaqahcode.muslimlauncher.data.remote.dto.SalawatDto
import com.sadaqahcode.muslimlauncher.data.remote.dto.SalawatResponseDto
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import javax.inject.Inject

class SalawatDtoMapper @Inject constructor() {

    fun toDomain(response: SalawatResponseDto): List<Salawat> =
        response.salawats.map { it.toDomain() }

    private fun SalawatDto.toDomain(): Salawat = Salawat(
        id = id,
        titles = titles,
        arabic = arabic,
        texts = texts,
        latin = latin,
        reference = reference,
        contexts = contexts,
    )
}
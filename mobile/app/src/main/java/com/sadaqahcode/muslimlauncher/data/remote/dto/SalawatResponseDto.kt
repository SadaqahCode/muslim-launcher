package com.sadaqahcode.muslimlauncher.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalawatResponseDto(
    @SerialName("salawats") val salawat: List<SalawatDto> = emptyList(),
)

@Serializable
data class SalawatDto(
    val id: Int,
    @SerialName("title_en") val titleEn: String? = null,
    @SerialName("title_id") val titleId: String? = null,
    val arabic: String = "",
    @SerialName("text_en") val textEn: String? = null,
    @SerialName("text_id") val textId: String? = null,
    val latin: String? = null,
    val reference: String? = null,
    @SerialName("context_en") val contextEn: String? = null,
    @SerialName("context_id") val contextId: String? = null,
)
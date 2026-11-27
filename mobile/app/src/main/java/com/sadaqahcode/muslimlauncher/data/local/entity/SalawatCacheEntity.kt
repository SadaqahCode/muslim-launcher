package com.sadaqahcode.muslimlauncher.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class SalawatCacheEntity(
    val items: List<SalawatCacheItem> = emptyList(),
    val lastUpdated: Long = 0L,
)

@Serializable
data class SalawatCacheItem(
    val id: Int,
    val titles: Map<String, String> = emptyMap(),
    val arabic: String = "",
    val texts: Map<String, String> = emptyMap(),
    val latin: String? = null,
    val reference: String? = null,
    val contexts: Map<String, String> = emptyMap(),
)
package com.sadaqahcode.muslimlauncher.data.local.entity

import kotlinx.serialization.Serializable

/**
 * Mirrors the *domain* shape (denormalized): each [DhikrItemCache] already
 * carries its full [DhikrEntryCache] list with the phrase embedded, so a
 * cache read does not need to re-join against a phrases table.
 *
 * Localized fields use Map<String, String> so the cache automatically stores
 * every language the server sends, with no schema change here when a new
 * language column appears.
 */
@Serializable
data class DhikrCacheEntity(
    val items: List<DhikrItemCache> = emptyList(),
    val lastUpdated: Long = 0L,
)

@Serializable
data class DhikrItemCache(
    val id: Long,
    val names: Map<String, String> = emptyMap(),
    val texts: Map<String, String> = emptyMap(),
    val position: Int = 0,
    val entries: List<DhikrEntryCache> = emptyList(),
)

@Serializable
data class DhikrEntryCache(
    val dhikr: DhikrPhraseCache,
    val repetitions: Int = 1,
)

@Serializable
data class DhikrPhraseCache(
    val id: Int,
    val titles: Map<String, String> = emptyMap(),
    val arabic: String = "",
    val texts: Map<String, String> = emptyMap(),
    val latin: String? = null,
    val reference: String? = null,
)
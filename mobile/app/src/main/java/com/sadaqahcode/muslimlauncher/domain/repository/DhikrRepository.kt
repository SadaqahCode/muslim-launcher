package com.sadaqahcode.muslimlauncher.domain.repository

import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.DhikrItem
import kotlinx.coroutines.flow.Flow

/**
 * Data access contract for dhikr content.
 *
 * Cache policy lives in the implementation. Callers interact with three
 * entry points:
 *
 *  - [getDhikrItems]      reactive: cache first, then network refresh
 *  - [refreshDhikrItems]  one-shot: clear, fetch, save, return
 *  - [getCachedOnly]      one-shot: read the cache without touching the network
 *
 * A "dhikr item" is a named group (e.g. "Dhikr After Salat (2)") containing
 * one or more [DhikrEntry] phrases with their repetition counts. Language
 * resolution happens at the UI edge: every phrase carries a
 * `Map<String, String>` per localized field, so the repository never needs
 * a `language` parameter and one cache serves every locale.
 */
interface DhikrRepository {

    /**
     * Emits the catalogue.
     *
     * When [tryOnlineFirst] is `false` (default):
     *   1. If a cache exists, it is emitted immediately — even if stale.
     *   2. The network is always attempted next; a successful fetch is saved
     *      and emitted as a second [Result.Success].
     *   3. A network failure after a successful cache emission is swallowed,
     *      because the user already has content on screen.
     *
     * When [tryOnlineFirst] is `true`:
     *   1. [Result.Loading] is emitted immediately.
     *   2. Network fetch is attempted. On success, the cache is written and
     *      [Result.Success] is emitted.
     *   3. On failure, cached content is emitted if present; otherwise
     *      [Result.Error] with a user-facing message.
     *
     * @param cacheValiditySeconds how old the cached copy may be before it is
     *   considered stale. Stale cache is still emitted, but signals that a
     *   refresh is worth waiting on. `0` = always stale, negative = never
     *   stale (cache-only reads with no refresh attempt).
     */
    fun getDhikrItems(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = DEFAULT_CACHE_VALIDITY_SECONDS,
    ): Flow<Result<List<DhikrItem>>>

    /**
     * Hard refresh: clears the cache, fetches from the network, saves, and
     * returns the fresh list.
     *
     * On network failure, the previous cache is preserved and returned as
     * [Result.Success]; only when both the network and the cache are empty
     * does this return [Result.Error].
     */
    suspend fun refreshDhikrItems(): Result<List<DhikrItem>>

    /**
     * Reads the cache without any network access. Returns [Result.Error] with
     * a user-facing message when nothing has been cached yet.
     */
    suspend fun getCachedOnly(): Result<List<DhikrItem>>

    /** Wipes the cache. Nothing else changes. */
    suspend fun clearCache()

    companion object {
        /**
         * Default freshness window for the catalogue. Dhikr content changes
         * rarely, and each fetch re-downloads the full multi-language payload,
         * so the default is deliberately generous. Screens that need fresher
         * data pass a lower value to [getDhikrItems] explicitly.
         */
        const val DEFAULT_CACHE_VALIDITY_SECONDS = 3_600L
    }
}
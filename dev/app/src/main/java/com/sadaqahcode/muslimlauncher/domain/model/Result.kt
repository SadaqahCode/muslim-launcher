package com.sadaqahcode.muslimlauncher.domain.model

/**
 * Result wrapper for use-case output. Unlike `kotlin.Result`, this carries a
 * [Loading] state so the UI can distinguish "first load, nothing yet" from
 * "refresh, cache already shown".
 */
sealed interface Result<out T> {

    /** Request in flight, no previous data to show. */
    data object Loading : Result<Nothing>

    /** Content available (may be stale cache or fresh network). */
    data class Success<T>(val data: T) : Result<T>

    /** Failure with a user-facing [message] resolved at the data layer. */
    data class Error(
        val exception: Throwable,
        val message: String? = null,
    ) : Result<Nothing>
}
package com.sadaqahcode.muslimlauncher.domain.model.tools

/**
 * A single dhikr phrase.
 *
 * Localized fields are maps keyed by language code ("en", "id", "ar", …).
 * No language is privileged — callers resolve with [title]/[text].
 */
data class Dhikr(
    val id: Int,
    val titles: Map<String, String>,
    val arabic: String,
    val texts: Map<String, String>,
    val latin: String?,
    val reference: String?,
)

/** Resolve the title for [language], falling back to English, then any entry. */
fun Dhikr.title(language: String): String =
    titles[language] ?: titles["en"] ?: titles.values.firstOrNull().orEmpty()

/** Resolve the body text for [language], same fallback chain. */
fun Dhikr.text(language: String): String? =
    texts[language] ?: texts["en"] ?: texts.values.firstOrNull()
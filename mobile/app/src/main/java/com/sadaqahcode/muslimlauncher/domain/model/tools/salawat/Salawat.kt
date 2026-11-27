package com.sadaqahcode.muslimlauncher.domain.model.tools.salawat

/**
 * A single salawat.
 *
 * Localized fields are maps keyed by language code ("en", "id", "ar", …).
 * No language is privileged — callers resolve with [title]/[text]/[context].
 */
data class Salawat(
    val id: Int,
    val titles: Map<String, String>,
    val arabic: String,
    val texts: Map<String, String>,
    val latin: String?,
    val reference: String?,
    /** Optional: background of the narration. */
    val contexts: Map<String, String>,
)

/** Resolve the title for [language], falling back to English, then any entry. */
fun Salawat.title(language: String): String =
    titles[language] ?: titles["en"] ?: titles.values.firstOrNull().orEmpty()

fun Salawat.text(language: String): String? = texts.resolve(language)

fun Salawat.context(language: String): String? = contexts.resolve(language)

private fun Map<String, String>.resolve(language: String): String? =
    this[language] ?: this["en"] ?: values.firstOrNull()
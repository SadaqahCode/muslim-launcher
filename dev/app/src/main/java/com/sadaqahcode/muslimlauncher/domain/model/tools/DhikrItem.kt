package com.sadaqahcode.muslimlauncher.domain.model.tools

/**
 * A group of dhikr ("Daily Istighfar 100x", "Dhikr After Salat (3)", …).
 * Localized fields are maps keyed by language code, same as [Dhikr].
 */
data class DhikrItem(
    val id: Long,
    val names: Map<String, String>,
    val texts: Map<String, String>,
    val position: Int,
    val entries: List<DhikrEntry>,
)

fun DhikrItem.name(language: String): String =
    names[language] ?: names["en"] ?: names.values.firstOrNull().orEmpty()

fun DhikrItem.text(language: String): String? =
    texts[language] ?: texts["en"] ?: texts.values.firstOrNull()
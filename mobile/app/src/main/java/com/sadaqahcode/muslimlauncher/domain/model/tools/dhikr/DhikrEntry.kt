package com.sadaqahcode.muslimlauncher.domain.model.tools

/** A dhikr plus how many times it is repeated inside a [DhikrItem] group. */
data class DhikrEntry(
    val dhikr: Dhikr,
    val repetitions: Int,
)
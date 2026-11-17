package com.sadaqahcode.muslimlauncher.domain.model

enum class AppCategory(val displayName: String, val hint: String) {
    DEEN("Deen", "Quran, prayer, learning, zakat"),
    DUNYA_NECESSARY("Necessary", "Work, banking, family, maps"),
    DUNYA_OPTIONAL("Optional", "Shopping, news, entertainment"),
    FITNAH_RISK("Risk", "Social feeds, dating, short video, gossip"),
    ;

    val isLowFriction: Boolean
        get() = this == DEEN || this == DUNYA_NECESSARY
}
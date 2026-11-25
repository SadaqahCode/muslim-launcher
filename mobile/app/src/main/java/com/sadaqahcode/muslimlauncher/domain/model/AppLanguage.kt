package com.sadaqahcode.muslimlauncher.domain.model

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val isRtl: Boolean = false,
) {
    English("en", "English"),
    Indonesian("id", "Bahasa Indonesia"),
    Arabic("ar", "العربية", isRtl = true),
    Malay("ms", "Bahasa Melayu"),
    Urdu("ur", "اردو", isRtl = true),
    Hindi("hi", "हिन्दी"),
    ;

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: English
    }
}
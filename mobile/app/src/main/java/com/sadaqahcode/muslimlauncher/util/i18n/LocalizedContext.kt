package com.sadaqahcode.muslimlauncher.util.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sadaqahcode.muslimlauncher.domain.model.AppLanguage
import java.util.Locale

fun Context.withLanguage(language: AppLanguage): Context {
    val locale = Locale.forLanguageTag(language.code)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

@Composable
fun rememberLocalizedContext(language: AppLanguage): Context {
    val base = LocalContext.current
    return remember(language) { base.withLanguage(language) }
}
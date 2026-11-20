package com.sadaqahcode.muslimlauncher.data.utils

import android.os.Build
import com.sadaqahcode.muslimlauncher.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.UUID

class ProductionHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val userAgent = "MuslimLauncher/${BuildConfig.VERSION_NAME} (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})"
        val language = Locale.getDefault().toLanguageTag()

        val requestBuilder = originalRequest.newBuilder()
            .header("X-App-Version", BuildConfig.VERSION_NAME)
            .header("X-App-Build", BuildConfig.VERSION_CODE.toString())
            .header("User-Agent", userAgent)
            .header("X-Platform", "android")
            .header("Accept-Language", language)
            .header("X-Request-ID", UUID.randomUUID().toString())

        return chain.proceed(requestBuilder.build())
    }
}
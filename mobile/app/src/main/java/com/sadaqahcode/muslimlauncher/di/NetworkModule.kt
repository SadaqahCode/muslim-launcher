package com.sadaqahcode.muslimlauncher.di

import com.flagodna.debugprint.DebugPrint
import com.sadaqahcode.muslimlauncher.BuildConfig
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiConfig
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiService
import com.sadaqahcode.muslimlauncher.data.utils.HmacSigningInterceptor
import com.sadaqahcode.muslimlauncher.data.utils.ProductionHeaderInterceptor
import com.sadaqahcode.muslimlauncher.data.utils.SessionAuthenticator
import com.sadaqahcode.muslimlauncher.data.utils.SessionInterceptor
import com.sadaqahcode.muslimlauncher.service.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val LOG_TAG = "NetworkModule"
    private const val TIMEOUT_SECONDS = 10L

    // true = log, false = no log anywhere
    private val ENABLE_HTTP_LOG = false // or BuildConfig.DEBUG

    @Provides
    @Singleton
    fun provideProductionHeaderInterceptor(): ProductionHeaderInterceptor =
        ProductionHeaderInterceptor()

    @Provides
    @Singleton
    fun provideHmacSigningInterceptor(): HmacSigningInterceptor =
        HmacSigningInterceptor()

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()

    @Provides
    @Singleton
    fun provideSessionInterceptor(
        sessionManager: SessionManager,
        @Named("challenge") challengeClient: OkHttpClient
    ): SessionInterceptor =
        SessionInterceptor(sessionManager, challengeClient)

    @Provides
    @Singleton
    fun provideSessionAuthenticator(
        sessionManager: SessionManager,
        @Named("challenge") challengeClient: OkHttpClient
    ): SessionAuthenticator =
        SessionAuthenticator(sessionManager, challengeClient)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message -> DebugPrint.d(LOG_TAG, message) }
            .apply {
                level = if (ENABLE_HTTP_LOG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

    // ── Bare client for challenge, NO interceptors, NO authenticator, to avoid loop ──
    @Provides
    @Singleton
    @Named("challenge")
    fun provideChallengeClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                // Use same flag, so one file controls it
                if (ENABLE_HTTP_LOG) addInterceptor(loggingInterceptor)
            }
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        productionHeaderInterceptor: ProductionHeaderInterceptor,
        hmacSigningInterceptor: HmacSigningInterceptor,
        sessionInterceptor: SessionInterceptor,
        sessionAuthenticator: SessionAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(productionHeaderInterceptor)
            .addInterceptor(hmacSigningInterceptor)
            .addInterceptor(sessionInterceptor)
            .apply {
                if (ENABLE_HTTP_LOG) addInterceptor(loggingInterceptor)
            }
            .authenticator(sessionAuthenticator)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideApiService(
        okHttpClient: OkHttpClient,
        json: Json,
    ): ApiService {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
}
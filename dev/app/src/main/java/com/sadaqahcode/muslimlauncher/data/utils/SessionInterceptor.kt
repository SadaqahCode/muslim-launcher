package com.sadaqahcode.muslimlauncher.data.utils

import com.flagodna.debugprint.DebugPrint
import com.sadaqahcode.muslimlauncher.BuildConfig
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiConfig
import com.sadaqahcode.muslimlauncher.service.HmacKeyManager
import com.sadaqahcode.muslimlauncher.service.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val LOG_TAG = "SessionInterceptor"

@Singleton
class SessionInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    @Named("challenge") private val challengeClient: OkHttpClient
) : Interceptor {

    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val path = request.url.encodedPath
        if (path.contains("/challenge")) return chain.proceed(request)

        var session = sessionManager.getSessionNonce()
        if (session == null) {
            synchronized(lock) {
                session = sessionManager.getSessionNonce()
                if (session == null) {
                    val fetched = fetchChallenge()
                    if (fetched != null) {
                        sessionManager.save(fetched.first, fetched.second)
                        session = fetched.first
                    }
                }
            }
        }

        if (session != null) {
            request = request.newBuilder().header("X-Nonce", session!!).build()
        }
        return chain.proceed(request)
    }

    private fun fetchChallenge(): Pair<String, Long>? {
        return try {
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val method = "GET"
            val path = "/${ApiConfig.API_VERSION}/challenge/get"
            val body = ""
            val stringToSign = "$timestamp\n$method\n$path\n$body"
            val signature = HmacKeyManager.sign(stringToSign) ?: return null

            val request = Request.Builder()
                .url(ApiConfig.BASE_URL + ApiConfig.API_VERSION + "/challenge/get")
                .get()
                .header("X-Platform", "android")
                .header("X-App-Version", BuildConfig.VERSION_NAME)
                .header("X-Timestamp", timestamp)
                .header("X-Signature", signature)
                .header("X-Nonce", UUID.randomUUID().toString())
                .build()

            val resp = challengeClient.newCall(request).execute()
            val respBody = resp.body?.string() ?: ""
            if (!resp.isSuccessful) return null
            val data = JSONObject(respBody).getJSONObject("data")
            data.getString("sessionNonce") to data.getLong("expiresAt")
        } catch (e: Exception) {
            DebugPrint.e(LOG_TAG, "fetchChallenge exception: ${e.message}")
            null
        }
    }
}
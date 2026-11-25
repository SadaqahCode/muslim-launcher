package com.sadaqahcode.muslimlauncher.data.utils

import com.flagodna.debugprint.DebugPrint
import com.sadaqahcode.muslimlauncher.BuildConfig
import com.sadaqahcode.muslimlauncher.data.remote.api.ApiConfig
import com.sadaqahcode.muslimlauncher.service.HmacKeyManager
import com.sadaqahcode.muslimlauncher.service.SessionManager
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val LOG_TAG = "SessionAuthenticator"

@Singleton
class SessionAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    @Named("challenge") private val challengeClient: OkHttpClient
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.contains("/challenge")) return null
        if (responseCount(response) >= 2) return null

        synchronized(lock) {
            val currentNonce = response.request.header("X-Nonce")
            val freshNonce = sessionManager.getSessionNonce()
            if (freshNonce != null && freshNonce != currentNonce) {
                return response.request.newBuilder()
                    .header("X-Nonce", freshNonce)
                    .build()
            }
            val newSession = fetchChallenge() ?: return null
            sessionManager.save(newSession.first, newSession.second)
            return response.request.newBuilder()
                .header("X-Nonce", newSession.first)
                .build()
        }
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
            if (!resp.isSuccessful) return null
            val bodyText = resp.body?.string() ?: return null
            val data = JSONObject(bodyText).getJSONObject("data")
            data.getString("sessionNonce") to data.getLong("expiresAt")
        } catch (e: Exception) {
            DebugPrint.e(LOG_TAG, "fetchChallenge exception: ${e.message}")
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
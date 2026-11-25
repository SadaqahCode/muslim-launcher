package com.sadaqahcode.muslimlauncher.data.utils

import com.flagodna.debugprint.DebugPrint
import com.sadaqahcode.muslimlauncher.service.HmacKeyManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HmacSigningInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val TAG = "Hmac"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // ── HMAC ONLY if method tagged HMAC ──
        val authType = originalRequest.tag(AuthType::class.java)
        if (authType != AuthType.HMAC) {
            return chain.proceed(originalRequest)
        }

        val bodyString = originalRequest.body?.let { body ->
            Buffer().also { body.writeTo(it) }.readUtf8()
        } ?: ""

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val nonce = UUID.randomUUID().toString()
        val path = originalRequest.url.encodedPath
        val method = originalRequest.method

        val stringToSign = "$timestamp\n$method\n$path\n$bodyString"
        DebugPrint.d(TAG, "sign message=[${stringToSign.replace("\n", "|")}]")

        val signatureHex = try {
            HmacKeyManager.sign(stringToSign)
                ?: throw IllegalStateException("No HMAC key")
        } catch (e: Exception) {
            DebugPrint.e(TAG, "Signing failed, sending unsigned: ${e.message}")
            return chain.proceed(originalRequest)
        }

        val signedRequest = originalRequest.newBuilder()
            .header("X-Timestamp", timestamp)
            .header("X-Nonce", nonce)
            .header("X-Signature", signatureHex)
            .build()

        return chain.proceed(signedRequest)
    }
}
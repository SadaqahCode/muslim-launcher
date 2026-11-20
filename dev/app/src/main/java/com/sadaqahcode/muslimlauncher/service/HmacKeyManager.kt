package com.sadaqahcode.muslimlauncher.service

import com.flagodna.debugprint.DebugPrint
import com.sadaqahcode.muslimlauncher.BuildConfig
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacKeyManager {

    private const val TAG = "Hmac"

    fun getKeyBytes(): ByteArray? {
        val key = BuildConfig.HMACK
        if (key.isEmpty()) {
            DebugPrint.e(TAG, "HMACK empty")
            return null
        }
        val bytes = key.toByteArray(Charsets.UTF_8)
        DebugPrint.d(TAG, "HMACK len=${key.length} bytes=${bytes.size} prefix=${key.take(4)}***")
        if (bytes.size != 64) {
            DebugPrint.e(TAG, "HMACK wrong bytes, expected 64, got ${bytes.size}")
            return null
        }
        return bytes
    }

    fun sign(message: String): String? {
        val keyBytes = getKeyBytes() ?: run {
            DebugPrint.e(TAG, "sign: no key")
            return null
        }
        DebugPrint.d(TAG, "sign message=[${message.replace("\n", "|")}]")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(keyBytes, "HmacSHA256"))
        val sig = mac.doFinal(message.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        DebugPrint.d(TAG, "sign sig prefix=${sig.take(8)}*** len=${sig.length}")
        return sig
    }
}
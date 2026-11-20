package com.sadaqahcode.muslimlauncher

import android.app.Application
import com.sadaqahcode.muslimlauncher.service.HmacKeyManager
import com.flagodna.debugprint.DebugPrint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MuslimLauncherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val bytes = HmacKeyManager.getKeyBytes()
        if (bytes == null) {
            DebugPrint.e("Hmac", "HMACK missing / invalid, HMAC disabled")
        } else {
            DebugPrint.d("Hmac", "HMACK OK, bytes=${bytes.size} (expected 64)")
        }
    }
}
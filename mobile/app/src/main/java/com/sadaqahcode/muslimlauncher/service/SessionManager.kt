package com.sadaqahcode.muslimlauncher.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    @Volatile private var sessionNonce: String? = null
    @Volatile private var expiresAt: Long = 0

    fun save(sessionNonce: String, expiresAt: Long) {
        this.sessionNonce = sessionNonce
        this.expiresAt = expiresAt
    }

    fun getSessionNonce(): String? {
        val nonce = sessionNonce ?: return null
        if (System.currentTimeMillis() / 1000 >= expiresAt) return null
        return nonce
    }

    fun getExpiresAt(): Long = expiresAt
    fun isExpired(): Boolean = getSessionNonce() == null
    fun clear() { sessionNonce = null; expiresAt = 0 }
}
package com.sadaqahcode.muslimlauncher.domain.model

data class ChallengeSession(
    val sessionNonce: String,
    val expiresAt: Long,
    val ttlSeconds: Int = 900
) {
    fun isExpired(nowSeconds: Long = System.currentTimeMillis() / 1000): Boolean {
        return nowSeconds >= expiresAt
    }
}
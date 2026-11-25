package com.sadaqahcode.muslimlauncher.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChallengeResponseDto(
    val success: Boolean = false,
    val message: String? = null,
    val data: ChallengeDataDto? = null,
    val error: String? = null
)

@Serializable
data class ChallengeDataDto(
    val sessionNonce: String,
    val expiresAt: Long,
    val ttlSeconds: Int = 900
)
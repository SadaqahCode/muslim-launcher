package com.sadaqahcode.muslimlauncher.domain.repository

import com.sadaqahcode.muslimlauncher.domain.model.ChallengeSession
import com.sadaqahcode.muslimlauncher.domain.model.Result

interface ChallengeRepository {
    suspend fun getChallenge(): Result<ChallengeSession>
    suspend fun getValidSession(): Result<ChallengeSession>
}
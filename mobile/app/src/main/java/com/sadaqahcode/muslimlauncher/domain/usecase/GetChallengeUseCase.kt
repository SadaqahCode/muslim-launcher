package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.domain.model.ChallengeSession
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.repository.ChallengeRepository
import javax.inject.Inject

class GetChallengeUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<ChallengeSession> {
        return if (forceRefresh) {
            repository.getChallenge()
        } else {
            repository.getValidSession()
        }
    }
}
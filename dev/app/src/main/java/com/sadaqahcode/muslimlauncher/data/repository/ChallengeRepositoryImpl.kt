package com.sadaqahcode.muslimlauncher.data.repository

import com.sadaqahcode.muslimlauncher.data.remote.api.ApiService
import com.sadaqahcode.muslimlauncher.domain.model.ChallengeSession
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.repository.ChallengeRepository
import com.sadaqahcode.muslimlauncher.service.SessionManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ChallengeRepository {

    override suspend fun getChallenge(): Result<ChallengeSession> {
        return try {
            val dto = apiService.getChallenge()
            val data = dto.data

            if (!dto.success || data == null) {
                return Result.Error(
                    exception = IllegalStateException(dto.error ?: dto.message ?: "Challenge failed"),
                    message = dto.error ?: dto.message ?: "Challenge failed"
                )
            }

            val session = ChallengeSession(
                sessionNonce = data.sessionNonce,
                expiresAt = data.expiresAt,
                ttlSeconds = data.ttlSeconds
            )

            // Save for SessionInterceptor
            sessionManager.save(session.sessionNonce, session.expiresAt)

            Result.Success(session)
        } catch (e: HttpException) {
            val msg = when (e.code()) {
                429 -> "Too many requests, try again later"
                401 -> "HMAC failed"
                403 -> "Access denied"
                else -> "Challenge failed: ${e.code()}"
            }
            Result.Error(exception = e, message = msg)
        } catch (e: IOException) {
            Result.Error(exception = e, message = "No internet connection")
        } catch (e: Exception) {
            Result.Error(exception = e, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getValidSession(): Result<ChallengeSession> {
        // If cached session still valid, reuse, don't call API
        val cachedNonce = sessionManager.getSessionNonce()
        if (cachedNonce != null) {
            return Result.Success(
                ChallengeSession(
                    sessionNonce = cachedNonce,
                    expiresAt = sessionManager.getExpiresAt(),
                    ttlSeconds = 900
                )
            )
        }
        // Expired / missing -> fetch new
        return getChallenge()
    }
}
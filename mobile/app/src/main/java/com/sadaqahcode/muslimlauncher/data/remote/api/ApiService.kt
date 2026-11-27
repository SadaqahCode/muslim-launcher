package com.sadaqahcode.muslimlauncher.data.remote.api

import com.sadaqahcode.muslimlauncher.data.remote.dto.ChallengeResponseDto
import com.sadaqahcode.muslimlauncher.data.remote.dto.DhikrResponseDto
import com.sadaqahcode.muslimlauncher.data.remote.dto.SalawatResponseDto
import com.sadaqahcode.muslimlauncher.data.utils.AuthType
import retrofit2.http.GET
import retrofit2.http.Tag

interface ApiService {

    @GET(ApiConfig.API_VERSION + "/challenge/get")
    suspend fun getChallenge(
        @Tag authType: AuthType = AuthType.HMAC,
    ): ChallengeResponseDto

    @GET(ApiConfig.API_VERSION + "/dhikr/get")
    suspend fun getDhikrItems(
        @Tag authType: AuthType = AuthType.SESSION,
    ): DhikrResponseDto

    @GET(ApiConfig.API_VERSION + "/salawat/get")
    suspend fun getSalawat(
        @Tag authType: AuthType = AuthType.SESSION,
    ): SalawatResponseDto
}
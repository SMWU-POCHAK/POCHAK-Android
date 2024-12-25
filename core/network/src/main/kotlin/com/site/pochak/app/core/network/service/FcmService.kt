package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkFcm
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface FcmService {
    @POST(value = "api/v1/fcm/register")
    suspend fun registerFcmToken(
        @Body token: NetworkFcm
    ): NetworkResponse<Unit>

    @DELETE(value = "/api/v1/fcm")
    suspend fun deleteFcmToken(): NetworkResponse<Unit>
}
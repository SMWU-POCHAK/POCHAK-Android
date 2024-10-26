package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.NetworkToken
import com.site.pochak.app.core.network.token.AuthInterceptor
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * Token API
 *
 * @POST /api/v2/refresh: 토큰 갱신 API
 * - 토큰 갱신 API: [AuthInterceptor]에서 토큰 만료 시 호출
 */
interface TokenService {

    @POST(value = "api/v2/refresh")
    fun refreshToken(@HeaderMap headers: Map<String, String>): Call<NetworkResponse<NetworkToken>>
}
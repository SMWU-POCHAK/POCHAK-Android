package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.UserInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface LoginService {

    @GET(value = "google/login/{accessToken}")
    suspend fun googleLogin(@Path(value = "accessToken") accessToken: String): NetworkResponse<UserInfo>
}
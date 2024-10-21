package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Login API
 *
 *  @GET /google/login/{accessToken}: 구글 로그인 API
 *  @POST /api/v2/signup: 회원가입 API
 *  @POST /api/v2/refresh: 토큰 갱신 API
 *  @GET /api/v2/logout: 로그아웃 API
 *  @DELETE /api/v2/signout: 회원 탈퇴 API
 *
 */
interface LoginService {

    @GET(value = "google/login/{accessToken}")
    suspend fun googleLogin(@Path(value = "accessToken") accessToken: String): NetworkResponse<NetworkLoginInfo>
}

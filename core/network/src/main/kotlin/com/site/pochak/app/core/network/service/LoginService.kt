package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @Multipart
    @POST(value = "api/v2/signup")
    suspend fun signUp(
        @Part profileImage: MultipartBody.Part,

        @Query("name") name: String,
        @Query("email") email: String,
        @Query("handle") handle: String,
        @Query("message") message: String,
        @Query("socialId") socialId: String,
        @Query("socialType") socialType: String,
        // 애플 리프레쉬 토큰 (구글에는 해당되지 않음)
        @Query("socialRefreshToken") socialRefreshToken: String? = null
    ): NetworkResponse<NetworkLoginInfo>
}

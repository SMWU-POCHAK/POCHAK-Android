package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkProfile
import com.site.pochak.app.core.network.model.NetworkProfileResult
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.PostPageResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *  Profile API
 *
 *  @GET /api/v2/members/{handle}: 프로필 조회 API
 *  @GET /api/v2/members/{handle}/upload: 업로드한 게시물 조회 API
 *  @GET /api/v2/members/duplicate: 닉네임 중복 확인 API
 *
 */
interface ProfileService {

    @GET(value = "api/v2/members/{handle}")
    suspend fun getProfile(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int,
    ): NetworkResponse<NetworkProfile>

    @GET(value = "api/v2/members/{handle}")
    suspend fun getPochakedPosts(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int,
    ): NetworkResponse<PostPageResponse>

    @GET(value = "api/v2/members/{handle}/upload")
    suspend fun getPochakPosts(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int,
    ): NetworkResponse<PostPageResponse>

    @PUT(value = "api/v2/members/{handle}")
    suspend fun updateProfile(
        @Path(value = "handle") handle: String,
        @Part postImage: MultipartBody.Part,
        @Query("name") name: String,
        @Query("message") message: String,
    ): NetworkResponse<NetworkProfileResult>

    @GET(value = "api/v2/members/duplicate")
    suspend fun checkDuplicateHandle(@Query(value = "handle") handle: String): NetworkResponse<Unit>
}

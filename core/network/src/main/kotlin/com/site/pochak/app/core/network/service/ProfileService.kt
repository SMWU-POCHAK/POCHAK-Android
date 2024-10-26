package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.GET
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

    @GET(value = "api/v2/members/duplicate")
    suspend fun checkDuplicateHandle(@Query(value = "handle") handle: String): NetworkResponse<Unit>
}

package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Search API
 *
 * @GET /api/v2/members/search: 멤버 검색 API
 *
 */
interface SearchService {

    @GET(value = "api/v2/members/search")
    suspend fun searchMembers(
        @Query(value = "keyword") keyword: String,
        @Query(value = "page") page: Int
    ): NetworkResponse<MemberPageResponse>

}
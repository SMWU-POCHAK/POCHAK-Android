package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Tag API
 *
 * @GET /api/v2/tags/{tagId}: 게시물 수락 API
 *
 */
interface TagService {

    @GET(value = "api/v2/tags/{tagId}")
    suspend fun approveTag(
        @Query(value = "tagId") tagId: Int,
        @Query(value = "isAccept") isAccept: Boolean
    ): NetworkResponse<Unit>

}
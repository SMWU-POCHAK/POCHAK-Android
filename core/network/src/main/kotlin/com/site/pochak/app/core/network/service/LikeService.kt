package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkMemberLike
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Like API
 *
 * @GET /api/v2/posts/{postId}/like: 좋아요 누른 사람들 조회 API
 * @POST /api/v2/posts/{postId}/like: 좋아요 누르기 API
 *
 */
interface LikeService {

    @GET(value = "api/v2/posts/{postId}/like")
    suspend fun getLikeMembers(
        @Path(value = "postId") postId: Int,
        @Query(value = "page") page: Int
    ): NetworkResponse<List<NetworkMemberLike>>

    @POST(value = "api/v2/posts/{postId}/like")
    suspend fun postLike(
        @Path(value = "postId") postId: Int
    ): NetworkResponse<Unit>

}
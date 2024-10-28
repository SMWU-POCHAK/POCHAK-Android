package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Follow API
 *
 * @GET /api/v2/members/{handle}/following: 팔로잉 조회 API
 * @GET /api/v2/members/{handle}/follower: 팔로워 조회 API
 * @POST /api/v2/members/{handle}/follow: 팔로우 API
 * @DELETE /api/v2/members/{handle}/follower: 팔로워 삭제 API
 *
 */
interface FollowService {

    @GET(value = "api/v2/members/{handle}/following")
    suspend fun getFollowing(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int
    ): NetworkResponse<MemberPageResponse>

    @GET(value = "api/v2/members/{handle}/follower")
    suspend fun getFollower(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int
    ): NetworkResponse<MemberPageResponse>

    @POST(value = "api/v2/members/{handle}/follow")
    suspend fun followMember(
        @Path(value = "handle") handle: String
    ): NetworkResponse<Unit>

    @DELETE(value = "api/v2/members/{handle}/follower")
    suspend fun unfollowMember(
        @Path(value = "handle") handle: String,
        @Query(value = "followerHandle") followerHandle: String
    ): NetworkResponse<Unit>

}
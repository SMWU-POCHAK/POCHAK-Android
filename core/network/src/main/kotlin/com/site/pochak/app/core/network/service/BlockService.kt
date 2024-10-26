package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.BlockPageResponse
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Block API
 *
 * @POST /api/v2/members/{handle}/block: 유저 차단 API
 * @GET /api/v2/members/{handle}/block: 차단 유저 조회 API
 * @DELETE /api/v2/members/{handle}/block: 유저 차단 해제 API
 *
 */
interface BlockService {

    @POST(value = "api/v2/members/{handle}/block")
    suspend fun blockMember(
        @Path(value = "handle") handle: String
    ): NetworkResponse<Unit>

    @GET(value = "api/v2/members/{handle}/block")
    suspend fun getBlockedUsers(
        @Path(value = "handle") handle: String,
        @Query(value = "page") page: Int
    ): NetworkResponse<BlockPageResponse>

    @DELETE(value = "api/v2/members/{handle}/block")
    suspend fun unblockUser(
        @Path(value = "handle") handle: String,
        @Query(value = "blockedMemberHandle") blockedMemberHandle: String
    ): NetworkResponse<Unit>

}
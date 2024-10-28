package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkMemory
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.PostPageResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Memories API
 *
 * @GET /api/v1/memories/{handle}: 추억 페이지 조회 API
 * @GET /api/v1/memories/{handle}/pochaked: 추억 페이지 Pochaked 조회 API
 * @GET /api/v1/memories/{handle}/bonded: 추억 페이지 Bonded 조회 API
 * @GET /api/v1/memories/{handle}/pochak: 추억 페이지 Pochak 조회 API
 *
 */
interface MemoriesService {

    @GET(value = "api/v1/memories/{handle}")
    suspend fun getMemories(
        @Path(value = "handle") handle: String
    ): NetworkResponse<NetworkMemory>

    @GET(value = "api/v1/memories/{handle}/pochaked")
    suspend fun getPochakedMemories(
        @Path(value = "handle") handle: String
    ): NetworkResponse<PostPageResponse>

    @GET(value = "api/v1/memories/{handle}/bonded")
    suspend fun getBondedMemories(
        @Path(value = "handle") handle: String
    ): NetworkResponse<PostPageResponse>

    @GET(value = "api/v1/memories/{handle}/pochak")
    suspend fun getPochakMemories(
        @Path(value = "handle") handle: String
    ): NetworkResponse<PostPageResponse>

}
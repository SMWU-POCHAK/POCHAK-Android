package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.PostPageResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Post API
 *
 * @GET /api/v2/posts: 홈 탭 조회 API
 * @GET /api/v2/posts/search: 탐색 탭 조회 API
 * @POST /api/v2/posts: 게시물 업로드 API
 * @GET /api/v2/posts/{postId}: 게시물 상세 페이지 조회 API
 * @DELETE /api/v2/posts/{postId}: 게시물 삭제 API
 *
 */
interface PostService {

    @GET(value = "api/v2/posts")
    fun getHomePosts(
        @Query(value = "page") page: Int
    ): NetworkResponse<PostPageResponse>

    @GET(value = "api/v2/posts/search")
    fun getSearchPosts(
        @Query(value = "page") page: Int
    ): NetworkResponse<PostPageResponse>

    @Multipart
    @POST(value = "api/v2/posts")
    fun postPost(
        @Part postImage: MultipartBody.Part,

        @Query("taggedMemberHandleList") taggedMemberHandleList: List<String>,
        @Query("caption") caption: String
    ): NetworkResponse<Unit>

    @GET(value = "api/v2/posts/{postId}")
    fun getPostDetail(
        @Query(value = "postId") postId: Int
    ): NetworkResponse<NetworkPostDetail>

    @DELETE(value = "api/v2/posts/{postId}")
    fun deletePost(
        @Query(value = "postId") postId: Int
    ): NetworkResponse<Unit>

}
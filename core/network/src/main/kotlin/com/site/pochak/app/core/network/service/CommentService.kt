package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.CommentPageResponse
import com.site.pochak.app.core.network.model.NetworkCommentBody
import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Comment API
 *
 * @POST /api/v2/posts/{postId}/comments: 댓글 업로드 API
 * @GET /api/v2/posts/{postId}/comments: 댓글 조회 API
 * @GET /api/v2/posts/{postId}/comments/{commentId}: 대댓글 페이징 조회용 API
 * @DELETE /api/v2/posts/{postId}/comments: 댓글 삭제 API
 *
 */
interface CommentService {

    @POST(value = "api/v2/posts/{postId}/comments")
    suspend fun uploadComment(
        @Path(value = "postId") postId: Int,
        @Body comment: NetworkCommentBody
    ): NetworkResponse<Unit>

    @GET(value = "api/v2/posts/{postId}/comments")
    suspend fun getComments(
        @Path(value = "postId") postId: Int,
        @Query(value = "page") page: Int,
        @Query(value = "sort") sort: String = "createdDate,desc"
    ): NetworkResponse<CommentPageResponse>

    @GET(value = "api/v2/posts/{postId}/comments/{commentId}")
    suspend fun getChildComments(
        @Path(value = "postId") postId: Int,
        @Path(value = "commentId") commentId: Int,
        @Query(value = "page") page: Int,
        @Query(value = "sort") sort: String = "createdDate,desc"
    ): NetworkResponse<ChildCommentPageResponse>

    @DELETE(value = "api/v2/posts/{postId}/comments")
    suspend fun deleteComment(
        @Path(value = "postId") postId: Int,
        @Query(value = "commentId") commentId: Int
    ): NetworkResponse<Unit>

}
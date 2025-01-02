package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.CommentPageResponse
import com.site.pochak.app.core.network.model.NetworkCommentWithChild
import com.site.pochak.app.core.network.utils.ApiResult

interface CommentRepository {
    suspend fun uploadComment(postId: Int, comment: String, parentCommentId: Int?): ApiResult

    /**
     * @return: [CommentPageResponse]
     */
    suspend fun getComments(postId: Int, page: Int = 0): ApiResult

    /**
     * @return: [NetworkCommentWithChild]
     */
    suspend fun getChildComments(
        postId: Int,
        commentId: Int,
        page: Int = 0
    ): ApiResult

    suspend fun deleteComment(postId: Int, commentId: Int): ApiResult

}
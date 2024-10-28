package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.CommentService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService
) : CommentRepository {
    override suspend fun uploadComment(postId: Int) =
        ApiResultHandler.handleResult {
            commentService.uploadComment(postId)
        }

    override suspend fun getComments(postId: Int, page: Int) =
        ApiResultHandler.handleResult {
            commentService.getComments(postId, page)
        }

    override suspend fun getChildComments(postId: Int, commentId: Int, page: Int) =
        ApiResultHandler.handleResult {
            commentService.getChildComments(postId, commentId, page)
        }

    override suspend fun deleteComment(postId: Int, commentId: Int) =
        ApiResultHandler.handleResult {
            commentService.deleteComment(postId, commentId)
        }

}
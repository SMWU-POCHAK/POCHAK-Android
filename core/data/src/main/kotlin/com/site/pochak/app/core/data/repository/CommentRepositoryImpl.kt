package com.site.pochak.app.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.site.pochak.app.core.data.paging.ItemPagingSource
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.model.NetworkCommentBody
import com.site.pochak.app.core.network.service.CommentService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService
) : CommentRepository {
    override val loginMemberProfileImage: MutableStateFlow<String> = MutableStateFlow("")

    override suspend fun uploadComment(postId: Int, comment: String, parentCommentId: Int?) =
        ApiResultHandler.handleResult {
            commentService.uploadComment(postId, NetworkCommentBody(comment, parentCommentId))
        }

    override fun getComments(postId: Int, page: Int): Flow<PagingData<ChildCommentPageResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = {
                ItemPagingSource<ChildCommentPageResponse> { page ->
                    ApiResultHandler.handleResult {
                        commentService.getComments(postId, page).let {
                            if (loginMemberProfileImage.value == "")
                                loginMemberProfileImage.value = it.result?.loginMemberProfileImage ?: ""
                            it
                        }
                    }
                }
            }
        ).flow
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
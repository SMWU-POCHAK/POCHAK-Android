package com.site.pochak.app.core.data.repository

import androidx.paging.PagingData
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.CommentPageResponse
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface CommentRepository {
    val loginMemberProfileImage: MutableStateFlow<String>

    suspend fun uploadComment(postId: Int, comment: String, parentCommentId: Int?): ApiResult

    /**
     * @return: [CommentPageResponse]
     */
    fun getComments(postId: Int, page: Int = 0): Flow<PagingData<ChildCommentPageResponse>>

    /**
     * @return: [NetworkCommentWithChild]
     */
    fun getChildComments(
        postId: Int,
        commentId: Int,
        page: Int = 0
    ): Flow<PagingData<NetworkComment>>

    suspend fun deleteComment(postId: Int, commentId: Int): ApiResult

}
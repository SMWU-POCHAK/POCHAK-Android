package com.site.pochak.app.feature.post.detail.comment

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.site.pochak.app.core.data.repository.CommentRepository
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository,
) : ViewModel() {
    private val postIdKey = "postId"
    private val PENDDING_DELETION_DELAY = 4000L

    private val route = saveStateHandle.toRoute<PostDetailRoute>()
    private val postId = saveStateHandle.getStateFlow(
        key = postIdKey,
        initialValue = route.postId
    )

    private val _childComments = mutableStateMapOf<Int, SnapshotStateList<NetworkComment>>()
    val childComments: Map<Int, SnapshotStateList<NetworkComment>> get() = _childComments

    private val childCommentPageInfo = mutableStateMapOf<Int, Int>()
    val childCommentPageIsLast = mutableStateMapOf<Int, MutableStateFlow<Boolean>>()

    val loginMemberProfileImage: StateFlow<String> = commentRepository.loginMemberProfileImage
    val commentList: Flow<PagingData<ChildCommentPageResponse>> = postId.flatMapLatest { postId ->
        commentRepository.getComments(postId).map { pagingData ->
            pagingData.map {
                if (it.childCommentList.isNotEmpty()) {
                    _childComments[it.commentId] = mutableStateListOf()
                    childCommentPageIsLast[it.commentId] = MutableStateFlow(false)
                }

                it.copy()
            }
        }
    }.cachedIn(viewModelScope)

    private val _pendingDeletion = MutableStateFlow<Job?>(null)
    val pendingDeletion: StateFlow<Job?> = _pendingDeletion.asStateFlow()

    fun loadChildComments(commentId: Int) {
        viewModelScope.launch {
            val page = childCommentPageInfo.getOrPut(commentId) { 0 }
            val result = commentRepository.getChildComments(postId.value, commentId, page)

            if (result is ApiResult.Success<*>) {
                val childCommentPageResponse = result.result as ChildCommentPageResponse

                _childComments[commentId]?.addAll(childCommentPageResponse.childCommentList)
                childCommentPageInfo[commentId] = page + 1
                childCommentPageIsLast[commentId]?.value = childCommentPageResponse.childCommentPageInfo.lastPage
            }
        }
    }

    fun deleteComment(commentId: Int, refresh: () -> Unit) {
        val job = viewModelScope.launch {
            delay(PENDDING_DELETION_DELAY)
            _pendingDeletion.value = null

            val result = commentRepository.deleteComment(postId.value, commentId)

            if (result is ApiResult.SuccessNoResult) {
                Log.d("CommentViewModel", "uploadComment: Success")
                refresh()
            } else {
                Log.e("CommentViewModel", "uploadComment: Error")
            }
        }

        _pendingDeletion.value = job
    }

    fun cancelPendingDeletion() {
        _pendingDeletion.value?.cancel()
        _pendingDeletion.value = null
    }


    fun uploadComment(content: String, commentId: Int?, refresh: () -> Unit) {
        viewModelScope.launch {
            val result = commentRepository.uploadComment(postId.value, content, commentId)

            if (result is ApiResult.SuccessNoResult) {
                Log.d("CommentViewModel", "uploadComment: Success")
                refresh()
            } else {
                Log.e("CommentViewModel", "uploadComment: Error")
            }
        }
    }
}
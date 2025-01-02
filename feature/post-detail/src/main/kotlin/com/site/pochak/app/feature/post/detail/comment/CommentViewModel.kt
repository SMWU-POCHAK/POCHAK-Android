package com.site.pochak.app.feature.post.detail.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.CommentRepository
import com.site.pochak.app.core.network.model.CommentPageResponse
import com.site.pochak.app.core.network.model.NetworkCommentWithChild
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository,
) : ViewModel() {
    private val postIdKey = "postId"

    private val route = saveStateHandle.toRoute<PostDetailRoute>()
    private val postId = saveStateHandle.getStateFlow(
        key = postIdKey,
        initialValue = route.postId
    )

    var myProfileImageUrl by mutableStateOf<String>("")
        private set

    private val _commentList = MutableStateFlow<List<NetworkCommentWithChild>>(emptyList())
    val commentList = _commentList.asStateFlow()

    private var page = 0
    private var isLastPage = false

    init {
        fetchCommentList()
    }

    private fun refreshCommentList() {
        _commentList.value = emptyList()
        page = 0
        isLastPage = false
        fetchCommentList()
    }

    fun fetchCommentList() {
        if (isLastPage) return

        viewModelScope.launch {
            when (val apiResult = commentRepository.getComments(postId.value, page)) {
                is ApiResult.Success<*> -> {
                    val commentWithChildPage = apiResult.result as CommentPageResponse

                    myProfileImageUrl = commentWithChildPage.loginMemberProfileImage
                    _commentList.value += commentWithChildPage.data

                    page++
                }

                else -> {
                    // Handle error
                }
            }
        }
    }

    fun upLoadComment(comment: String, parentCommentId: Int?) {
        viewModelScope.launch {
            when (val apiResult = commentRepository.uploadComment(postId.value, comment, parentCommentId)) {
                is ApiResult.SuccessNoResult -> {
                    refreshCommentList()
                }

                else -> {
                    // Handle error
                }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            when (val apiResult = commentRepository.deleteComment(postId.value, commentId)) {
                is ApiResult.SuccessNoResult -> {
                    refreshCommentList()
                }

                else -> {
                    // Handle error
                }
            }
        }
    }
}
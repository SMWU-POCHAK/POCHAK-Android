package com.site.pochak.app.feature.post.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.FollowRepository
import com.site.pochak.app.core.data.repository.LikeRepository
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.data.repository.ReportRepository
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val followRepository: FollowRepository,
    private val reportRepository: ReportRepository,
) : ViewModel() {
    private val postIdKey = "postId"
    private val LIKE_SUCCESS = "LIKE2001"
    private val FOLLOW_SUCCESS = "COMMON200"
    private val DELETE_SUCCESS = "POST2002"
    private val REPORT_SUCCESS = "REPORT2001"

    private val route = saveStateHandle.toRoute<PostDetailRoute>()
    private val postId = saveStateHandle.getStateFlow(
        key = postIdKey,
        initialValue = route.postId
    )

    var isFollow by mutableStateOf<Boolean?>(null)
        private set

    var isLike by mutableStateOf(false)
        private set

    val postDetailUiState: StateFlow<PostDetailUiState> = postId.map { postId ->
        when (val apiResult = postRepository.getPostDetail(postId)) {
            is ApiResult.Success<*> -> {
                val postDetail = apiResult.result as NetworkPostDetail

                isFollow = postDetail.isFollow
                isLike = postDetail.isLike

                PostDetailUiState.Success(postDetail)
            }

            else -> PostDetailUiState.Error
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PostDetailUiState.Loading
        )

    var postDetailDeleteState by mutableStateOf<PostDetailDeleteState>(PostDetailDeleteState.Idle)
        private set

    fun likePost() {
        viewModelScope.launch {
            isLike = !isLike

            val result = likeRepository.postLike(postId.value)

            if (result is ApiResult.SuccessNoResult) {
                if (result.code != LIKE_SUCCESS) {
                    Log.e("PostDetailViewModel", "Failed to like post")
                    isLike = !isLike
                }
            } else {
                Log.e("PostDetailViewModel", "Failed to like post")
                isLike = !isLike
            }
        }
    }

    fun followMember() {
        Log.d("PostDetailViewModel", "onClickFollow: $isFollow")
        isFollow?.let { isFollowNotNull ->
            viewModelScope.launch {
                val postDetailUiState = postDetailUiState.first()

                if (postDetailUiState is PostDetailUiState.Success) {
                    isFollow = !isFollowNotNull

                    val result = followRepository.followMember(postDetailUiState.postDetail.ownerHandle)

                    if (result is ApiResult.SuccessNoResult) {
                        if (result.code != FOLLOW_SUCCESS) {
                            Log.e("PostDetailViewModel", "Failed to follow member")
                            isFollow = isFollowNotNull
                        }
                    } else {
                        Log.e("PostDetailViewModel", "Failed to follow member")
                        isFollow = isFollowNotNull
                    }
                }
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            postDetailDeleteState = PostDetailDeleteState.Loading

            val result = postRepository.deletePost(postId.value)

            postDetailDeleteState = if (result is ApiResult.SuccessNoResult) {
                if (result.code == DELETE_SUCCESS) {
                    PostDetailDeleteState.Success
                } else {
                    Log.e("PostDetailViewModel", "Failed to delete post")
                    PostDetailDeleteState.Error
                }
            } else {
                Log.e("PostDetailViewModel", "Failed to delete post")
                PostDetailDeleteState.Error
            }
        }
    }

    fun reportPost(reportType: String) {
        viewModelScope.launch {
            val result = reportRepository.postReport(postId.value, reportType)

            if (result is ApiResult.SuccessNoResult) {
                if (result.code == REPORT_SUCCESS) {
                    Log.d("PostDetailViewModel", "Report success")
                } else {
                    Log.e("PostDetailViewModel", "Failed to report post")
                }
            } else {
                Log.e("PostDetailViewModel", "Failed to report post")
            }
        }
    }
}

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Success(val postDetail: NetworkPostDetail) : PostDetailUiState
    data object Error : PostDetailUiState
}

sealed interface PostDetailDeleteState {
    data object Idle : PostDetailDeleteState
    data object Loading : PostDetailDeleteState
    data object Success : PostDetailDeleteState
    data object Error : PostDetailDeleteState
}
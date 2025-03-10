package com.site.pochak.app.feature.post.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.data.repository.ReportRepository
import com.site.pochak.app.core.domain.FollowUseCase
import com.site.pochak.app.core.domain.LikeUseCase
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DELETE_SUCCESS = "POST2002"
private const val REPORT_SUCCESS = "REPORT2001"

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val followUseCase: FollowUseCase,
    private val likeUseCase: LikeUseCase,
    private val reportRepository: ReportRepository,
) : ViewModel() {
    private val postIdKey = "postId"

    private val route = saveStateHandle.toRoute<PostDetailRoute>()
    private val postId = saveStateHandle.getStateFlow(
        key = postIdKey,
        initialValue = route.postId
    )

    var isFollow by mutableStateOf<Boolean?>(null)
        private set

    var isLike by mutableStateOf(false)
        private set

    val uiState: StateFlow<PostDetailUiState> = postId.map { postId ->
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

    private val _actionState: MutableStateFlow<PostDetailActionState> = MutableStateFlow(PostDetailActionState.Idle)
    val actionState: StateFlow<PostDetailActionState> = _actionState.asStateFlow()
    

    fun likePost() {
        viewModelScope.launch {
            val postId = postId.first()

            likeUseCase(postId)
                .onStart { _actionState.value = PostDetailActionState.Loading }
                .collect {
                    _actionState.value = if (it) {
                        isLike = isLike.not()
                        PostDetailActionState.Idle
                    } else {
                        PostDetailActionState.Error("Failed to like post")
                    }
                }
        }
    }

    fun followMember() {
        isFollow?.let { isFollowNotNull ->
            viewModelScope.launch {
                val uiState = uiState.first()

                if (uiState is PostDetailUiState.Success) {
                    followUseCase(uiState.postDetail.ownerHandle)
                        .onStart { _actionState.value = PostDetailActionState.Loading }
                        .collect {
                            _actionState.value = if (it) {
                                isFollow = isFollowNotNull.not()
                                PostDetailActionState.Idle
                            } else {
                                PostDetailActionState.Error("Failed to follow member")
                            }
                        }
                }
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            _actionState.value = PostDetailActionState.Loading

            val result = postRepository.deletePost(postId.value)

            _actionState.value = if (result is ApiResult.SuccessNoResult) {
                if (result.code == DELETE_SUCCESS) {
                    PostDetailActionState.Delete
                } else {
                    PostDetailActionState.Error("Failed to delete post")
                }
            } else {
                PostDetailActionState.Error("Failed to delete post")
            }
        }
    }

    fun reportPost(reportType: String) {
        viewModelScope.launch {
            _actionState.value = PostDetailActionState.Loading

            val result = reportRepository.postReport(postId.value, reportType)

            _actionState.value = if (result is ApiResult.SuccessNoResult) {
                if (result.code == REPORT_SUCCESS) {
                    PostDetailActionState.Report
                } else {
                    PostDetailActionState.Error("Failed to report post")
                }
            } else {
                PostDetailActionState.Error("Failed to report post")
            }
        }
    }
}

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Success(val postDetail: NetworkPostDetail) : PostDetailUiState
    data object Error : PostDetailUiState
}

sealed interface PostDetailActionState {
    data object Idle : PostDetailActionState
    data object Loading : PostDetailActionState
    data object Delete : PostDetailActionState
    data object Report : PostDetailActionState
    data class Error(val message: String) : PostDetailActionState
}
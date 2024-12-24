package com.site.pochak.app.feature.post.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.LikeRepository
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository
) : ViewModel() {
    private val postIdKey = "postId"

    private val route = saveStateHandle.toRoute<PostDetailRoute>()
    private val postId = saveStateHandle.getStateFlow(
        key = postIdKey,
        initialValue = route.postId
    )

    val postDetailUiState: StateFlow<PostDetailUiState> = postId.map { postId ->
        when (val apiResult = postRepository.getPostDetail(postId)) {
            is ApiResult.Success<*> -> {
                val postDetail = apiResult.result as NetworkPostDetail

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

    fun onClickLike() {
        viewModelScope.launch {
//            likeRepository.postLike(postId.value)
        }
    }
}

sealed class PostDetailUiState {
    data object Loading : PostDetailUiState()
    data class Success(val postDetail: NetworkPostDetail) : PostDetailUiState()
    data object Error : PostDetailUiState()
}
package com.site.pochak.app.feature.post.detail.comment

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import androidx.paging.map
import com.site.pochak.app.core.data.repository.CommentRepository
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.post.detail.navigation.PostDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val loginMemberProfileImage: StateFlow<String> = commentRepository.loginMemberProfileImage
    val commentList: Flow<PagingData<ChildCommentPageResponse>> = postId.flatMapLatest { postId ->
        commentRepository.getComments(postId)
    }.cachedIn(viewModelScope)

    private val _childComments: MutableStateFlow<Map<Int, MutableList<NetworkComment>>> = MutableStateFlow(emptyMap())
    val childComments: StateFlow<Map<Int, List<NetworkComment>>> = _childComments

    // 각 부모 댓글의 페이지 추적
    private val childCommentPageNum = mutableMapOf<Int, Int>()


    fun deleteComment(commentId: Int) {

    }

    fun uploadComment(content: String, commentId: Int?) {

    }
}
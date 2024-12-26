package com.site.pochak.app.feature.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.data.util.PageResponseManager
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.network.model.NetworkPost
import com.site.pochak.app.core.network.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val postPageManager = PageResponseManager<NetworkPost>(
        fetchPage = postRepository::getSearchPosts,
        scope = viewModelScope
    )

    val postPosts: StateFlow<List<Post>> = postPageManager.data.map {
        it.map(NetworkPost::toModel)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val isLoading: StateFlow<Boolean> = postPageManager.isLoading
    val isRefreshing: StateFlow<Boolean> = postPageManager.isRefreshing

    fun loadPage(isRefresh: Boolean) {
        postPageManager.loadPage(isRefresh)
    }
}
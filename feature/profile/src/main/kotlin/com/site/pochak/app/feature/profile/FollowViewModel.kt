package com.site.pochak.app.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.site.pochak.app.core.data.repository.FollowRepository
import com.site.pochak.app.core.model.data.Member
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.model.toModel
import com.site.pochak.app.feature.profile.navigation.FollowRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    followRepository: FollowRepository,
) : ViewModel() {
    private val handleKey = "handle"
    private val followerCountKey = "followerCount"
    private val followingCountKey = "followingCount"
    private val selectedTabKey = "selectedTab"

    private val route = saveStateHandle.toRoute<FollowRoute>()
    private val handle = saveStateHandle.getStateFlow(
        key = handleKey,
        initialValue = route.handle
    )
    private val followerCount = saveStateHandle.getStateFlow(
        key = followerCountKey,
        initialValue = route.followerCount
    )
    private val followingCount = saveStateHandle.getStateFlow(
        key = followingCountKey,
        initialValue = route.followingCount
    )
    private val selectedTab = saveStateHandle.getStateFlow(
        key = selectedTabKey,
        initialValue = route.selectedTab
    )

    val uiState: StateFlow<FollowUiState> = combine(
        handle,
        followerCount,
        followingCount,
        selectedTab
    ) { handle, followerCount, followingCount, selectedTab ->
        FollowUiState.Success(
            handle = handle,
            followerCount = followerCount,
            followingCount = followingCount,
            selectedTab = selectedTab
        )
    }
        .map { it as FollowUiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FollowUiState.Loading
        )

    val followerList: StateFlow<PagingData<Member>> = handle.flatMapLatest { handle ->
        followRepository.getFollower(handle).map {
            it.map(NetworkMember::toModel)
        }
    }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    val followingList: StateFlow<PagingData<Member>> = handle.flatMapLatest { handle ->
        followRepository.getFollowing(handle).map {
            it.map(NetworkMember::toModel)
        }
    }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )
}

sealed interface FollowUiState {
    data object Loading : FollowUiState
    data class Success(
        val handle: String,
        val followerCount: Int,
        val followingCount: Int,
        val selectedTab: Int,
    ) : FollowUiState
    data class Error(val message: String) : FollowUiState
}
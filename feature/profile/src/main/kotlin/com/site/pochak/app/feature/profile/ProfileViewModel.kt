package com.site.pochak.app.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.domain.FollowUseCase
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.network.model.NetworkPost
import com.site.pochak.app.core.network.model.NetworkProfile
import com.site.pochak.app.core.network.model.toModel
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.profile.navigation.ProfileGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    tokenManager: TokenManager,
    private val profileRepository: ProfileRepository,
    private val followUseCase: FollowUseCase,
) : ViewModel() {
    private val handleKey = "handle"

    private val route = saveStateHandle.toRoute<ProfileGraph>()
    private val handle = saveStateHandle.getStateFlow(
        key = handleKey,
        initialValue = route.handle
    ).map {
        it ?: tokenManager.getUserHandle().first()
    }

    var isFollow by mutableStateOf<Boolean?>(null)
        private set

    val uiState: StateFlow<ProfileUiState> = handle.map { handle ->
        if (handle == null) {
            ProfileUiState.Error("Handle is null")
        } else {
            when (val apiResult = profileRepository.getProfile(handle, 0)) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as NetworkProfile

                    isFollow = result.isFollow

                    ProfileUiState.Success(result)
                }

                else -> ProfileUiState.Error("ApiResult is not Success")
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ProfileUiState.Loading
        )

    val pochakedPosts: StateFlow<PagingData<Post>> = handle.flatMapLatest { handle ->
        handle?.let { handle ->
            profileRepository.getPochakedPosts(handle).map {
                it.map(NetworkPost::toModel)
            }
        } ?: throw IllegalStateException("Handle is null")
    }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    val pochakPosts: StateFlow<PagingData<Post>> = handle.flatMapLatest { handle ->
        handle?.let { handle ->
            profileRepository.getPochakPosts(handle).map {
                it.map(NetworkPost::toModel)
            }
        } ?: throw IllegalStateException("Handle is null")
    }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun followMember() {
        isFollow?.let { isFollowNotNull ->
            viewModelScope.launch {
                val uiState = uiState.first()

                if (uiState is ProfileUiState.Success) {
                    followUseCase(uiState.profile.handle)
                        .collect {
                            if (it) {
                                isFollow = !isFollowNotNull
                            }
                        }
                }
            }
        }
    }
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: NetworkProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
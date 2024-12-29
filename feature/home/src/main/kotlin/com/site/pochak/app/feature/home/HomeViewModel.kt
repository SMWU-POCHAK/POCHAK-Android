package com.site.pochak.app.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.FcmRepository
import com.site.pochak.app.core.data.util.PageResponseManager
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.network.model.NetworkFcm
import com.site.pochak.app.core.network.model.NetworkPost
import com.site.pochak.app.core.network.model.toModel
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val fcmRepository: FcmRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val postPageManager = PageResponseManager<NetworkPost>(
        fetchPage = postRepository::getHomePosts,
        scope = viewModelScope
    )

    val homePosts: StateFlow<List<Post>> = postPageManager.data.map {
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

    // 토큰 전송 상태 업데이트
    fun updateTokenSent(sent: Boolean) {
        viewModelScope.launch {
            tokenManager.setFcmTokenSent(sent)

            // 값이 제대로 저장되었는지 확인
            delay(100) // 비동기 저장 완료 대기
            val isFcmTokenSent = tokenManager.getFcmTokenSent().first()
            Log.d(TAG, "isFcmTokenSent after update: $isFcmTokenSent")
        }
    }

    private val _fcmState = MutableLiveData<FcmUiState>()
    val fcmState: LiveData<FcmUiState> = _fcmState

    // FCM 토큰 서버 등록
    fun registerFcmToken(token: String) {
        viewModelScope.launch {
            // isTokenSent 확인
            val isFcmTokenSent = tokenManager.getFcmTokenSent().first()
            Log.d(TAG, "isFcmTokenSent: $isFcmTokenSent")
            if (isFcmTokenSent) {
                Log.d(TAG, "Token already sent to server. Skipping...")
                _fcmState.value = FcmUiState.Success("Token already sent")
                return@launch
            }

            _fcmState.value = FcmUiState.Loading

            val result = fcmRepository.registerFcmToken(NetworkFcm(token))
            _fcmState.value = when (result) {
                is ApiResult.SuccessNoResult -> {
                    // 성공적으로 전송 완료, 상태 업데이트
                    updateTokenSent(true)
                    FcmUiState.Success("Token registered successfully")
                }
                is ApiResult.Error -> {
                    FcmUiState.Error(result.message)
                }
                else -> {
                    FcmUiState.Error("Unknown error")
                }
            }

            Log.d(TAG, "FCM Token registration result: $result")
        }
    }
}

sealed class FcmUiState {
    object Loading : FcmUiState()
    data class Success(val message: String) : FcmUiState()
    data class Error(val message: String) : FcmUiState()
}
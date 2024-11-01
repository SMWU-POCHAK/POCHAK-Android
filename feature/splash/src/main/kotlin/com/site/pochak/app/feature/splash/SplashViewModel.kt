package com.site.pochak.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.domain.GoogleLoginState
import com.site.pochak.app.core.domain.GoogleLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    googleLoginUseCase: GoogleLoginUseCase
) : ViewModel() {
    private val SPLASH_DURATION = 2000L

    private val isDelayFinished: Flow<Boolean> = flow {
        delay(SPLASH_DURATION)
        emit(true)
    }

    val googleLoginState: StateFlow<SplashUiState> = googleLoginUseCase()
        .combine(isDelayFinished) { googleLoginState, isDelayFinished ->
            if (isDelayFinished) {
                when (googleLoginState) {
                    GoogleLoginState.Loading -> SplashUiState.Loading
                    GoogleLoginState.LoginSuccess -> SplashUiState.LoginSuccess
                    else -> SplashUiState.LoginFailed
                }
            } else {
                SplashUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = SplashUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )
}

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object LoginSuccess : SplashUiState
    data object LoginFailed : SplashUiState
}

package com.site.pochak.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.site.pochak.app.core.domain.GoogleLoginState
import com.site.pochak.app.core.domain.GoogleLoginUseCase
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: GoogleLoginUseCase,
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Login)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun googleLogin(account: GoogleSignInAccount?) {
        viewModelScope.launch {
            googleLoginUseCase(account).collect { googleLoginState ->
                _loginUiState.value = when (googleLoginState) {
                    GoogleLoginState.Loading -> LoginUiState.Loading
                    GoogleLoginState.LoginFailed -> LoginUiState.Error
                    is GoogleLoginState.SignUp -> LoginUiState.SignUp(googleLoginState.loginInfo)
                    GoogleLoginState.LoginSuccess -> LoginUiState.Success
                }
            }
        }
    }

    fun resetLoginUiState() {
        _loginUiState.value = LoginUiState.Login
    }
}

sealed interface LoginUiState {
    data object Login : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class SignUp(val loginInfo: NetworkLoginInfo) : LoginUiState
    data object Error : LoginUiState
}
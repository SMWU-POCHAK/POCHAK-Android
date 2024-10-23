package com.site.pochak.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Login)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun googleLogin(accessToken: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            _loginUiState.value = try {
                val response = loginRepository.googleLogin(accessToken)

                if (response.isSuccess) {
                    val loginInfo = response.result

                    if (loginInfo == null) {
                        LoginUiState.Error("Result is null")
                        return@launch
                    }

                    if (loginInfo.isNewMember) {
                        LoginUiState.SignUp(loginInfo)
                    } else {
                        // TokenManager에 AccessToken과 RefreshToken 저장
                        tokenManager.saveAccessToken(loginInfo.accessToken!!)
                        tokenManager.saveRefreshToken(loginInfo.refreshToken!!)
                        LoginUiState.Success
                    }
                } else {
                    LoginUiState.Error(response.message)
                }
            } catch (e: Exception) {
                LoginUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetLoginUiState() {
        _loginUiState.value = LoginUiState.Login
    }
}

sealed class LoginUiState {
    data object Login : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class SignUp(val loginInfo: NetworkLoginInfo) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
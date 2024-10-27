package com.site.pochak.app.feature.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

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
            _loginUiState.value = when (val apiResult = loginRepository.googleLogin(accessToken)) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as NetworkLoginInfo

                    if (result.isNewMember) {
                        LoginUiState.SignUp(result)
                    } else {
                        if (result.accessToken == null || result.refreshToken == null || result.handle == null) {
                            Log.e(
                                TAG,
                                "Server Response Error: /google/login response is missing AccessToken, RefreshToken, Handle"
                            )
                            LoginUiState.Error("AccessToken, RefreshToken, Handle is null")
                        } else {
                            // TokenManager에 AccessToken, RefreshToken, Handle 저장
                            tokenManager.saveUserData(
                                result.accessToken!!,
                                result.refreshToken!!,
                                result.handle!!
                            )

                            LoginUiState.Success
                        }
                    }
                }

                is ApiResult.Error -> {
                    LoginUiState.Error("${apiResult.code}: ${apiResult.message}")
                }

                else -> {
                    LoginUiState.Error("Unknown Error")
                }
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
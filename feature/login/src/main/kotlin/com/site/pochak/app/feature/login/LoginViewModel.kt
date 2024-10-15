package com.site.pochak.app.feature.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.network.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Login)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun googleLogin(accessToken: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            _loginUiState.value = try {
                val response = loginRepository.googleLogin(accessToken)

                if (response.isSuccess) {
                    val userInfo = response.result

                    if (userInfo.isNewMember) {
                        LoginUiState.SignUp(userInfo)
                    } else {
                        LoginUiState.Success
                    }
                } else {
                    LoginUiState.Error(response.message)
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "googleLogin error", e)
                LoginUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class LoginUiState {
    data object Login : LoginUiState()
    data object Loading : LoginUiState()
    data class SignUp(val userInfo: UserInfo) : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
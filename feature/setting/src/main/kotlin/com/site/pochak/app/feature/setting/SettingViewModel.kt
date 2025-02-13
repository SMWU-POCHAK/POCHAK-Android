package com.site.pochak.app.feature.setting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOGOUT_SUCCESS_CODE = "LOGIN2001"
private const val SIGNOUT_SUCCESS_CODE = "LOGIN2002"

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.Idle())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            if (_uiState.value !is SettingUiState.Idle) return@launch

            _uiState.value = SettingUiState.Loading
            _uiState.value = when (val apiResult = loginRepository.logout()) {
                is ApiResult.SuccessNoResult -> {
                    if (apiResult.code == LOGOUT_SUCCESS_CODE) {
                        tokenManager.resetUserData()
                        SettingUiState.Success
                    }
                    else {
                        SettingUiState.Idle(apiResult.code)
                    }
                }

                else -> {
                    SettingUiState.Idle("Logout Error")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            if (_uiState.value !is SettingUiState.Idle) return@launch

            _uiState.value = SettingUiState.Loading
            _uiState.value = when (val apiResult = loginRepository.signout()) {
                is ApiResult.SuccessNoResult -> {
                    if (apiResult.code == SIGNOUT_SUCCESS_CODE) {
                        tokenManager.resetUserData()
                        SettingUiState.Success
                    }
                    else {
                        SettingUiState.Idle(apiResult.code)
                    }
                }

                else -> {
                    SettingUiState.Idle("Signout Error")
                }
            }
        }
    }
}

sealed interface SettingUiState {
    data class Idle(val errorMessage: String? = null) : SettingUiState
    data object Loading : SettingUiState
    data object Success : SettingUiState
}
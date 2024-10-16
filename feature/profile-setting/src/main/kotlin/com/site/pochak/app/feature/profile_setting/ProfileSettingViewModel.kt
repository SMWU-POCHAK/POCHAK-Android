package com.site.pochak.app.feature.profile_setting

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.network.model.LoginInfo
import com.site.pochak.app.feature.profile_setting.navigation.ProfileSettingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository,
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val loginInfoKey = "loginInfoKey"

    private val profileSettingRoute = savedStateHandle.toRoute<ProfileSettingRoute>()
    private val loginInfo = savedStateHandle.getStateFlow(
        key = loginInfoKey,
        initialValue = Json.decodeFromString(LoginInfo.serializer(), profileSettingRoute.loginInfoJson)
    )

    private val _checkHandleUiState = MutableStateFlow<CheckHandleUiState>(CheckHandleUiState.Idle)
    val checkHandleUiState: StateFlow<CheckHandleUiState> = _checkHandleUiState.asStateFlow()

    fun checkDuplicateHandle(handle: String) {
        viewModelScope.launch {
            _checkHandleUiState.value = CheckHandleUiState.Loading
            _checkHandleUiState.value = try {
                val response = profileRepository.checkDuplicateHandle(handle)

                Log.d("ProfileSettingViewModel", "isPossible: ${response.isSuccess}")

                if (response.isSuccess) {
                    CheckHandleUiState.Success
                } else {
                    CheckHandleUiState.Error(response.message)
                }
            } catch (e: Exception) {
                CheckHandleUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetCheckHandleUiState() {
        _checkHandleUiState.value = CheckHandleUiState.Idle
    }

    fun onComplete(name: String, handle: String, message: String) {
        viewModelScope.launch {

        }
    }
}

sealed class CheckHandleUiState {
    data object Idle : CheckHandleUiState()
    data object Loading : CheckHandleUiState()
    data object Success : CheckHandleUiState()
    data class Error(val message: String) : CheckHandleUiState()
}
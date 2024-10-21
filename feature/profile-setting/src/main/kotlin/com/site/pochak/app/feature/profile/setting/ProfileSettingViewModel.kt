package com.site.pochak.app.feature.profile.setting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.feature.profile.setting.navigation.ProfileSettingRoute
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
    private val profileRepository: ProfileRepository
): ViewModel() {

    private val loginInfoKey = "loginInfo"

    private val route = savedStateHandle.toRoute<ProfileSettingRoute>()
    private val loginInfo = savedStateHandle.getStateFlow(
        key = loginInfoKey,
        initialValue = Json.decodeFromString(NetworkLoginInfo.serializer(), route.loginInfoJson)
    )

    private val _checkHandleUiState = MutableStateFlow<CheckHandleUiState>(CheckHandleUiState.UnChecked)
    val checkHandleUiState: StateFlow<CheckHandleUiState> = _checkHandleUiState.asStateFlow()

    fun checkHandle(handle: String) {
        viewModelScope.launch {
            _checkHandleUiState.value = CheckHandleUiState.Loading
            _checkHandleUiState.value = try {
                val response = profileRepository.checkDuplicateHandle(handle)

                if (response.isSuccess) {
                    CheckHandleUiState.Checked
                } else {
                    CheckHandleUiState.Error(response.message)
                }
            } catch (e: Exception) {
                CheckHandleUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetCheckHandleUiState() {
        _checkHandleUiState.value = CheckHandleUiState.UnChecked
    }
}

sealed class CheckHandleUiState {
    data object UnChecked: CheckHandleUiState()
    data object Loading: CheckHandleUiState()
    data object Checked: CheckHandleUiState()
    data class Error(val message: String): CheckHandleUiState()
}

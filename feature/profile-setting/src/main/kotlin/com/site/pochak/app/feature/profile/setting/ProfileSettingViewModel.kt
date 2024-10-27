package com.site.pochak.app.feature.profile.setting

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.profile.setting.navigation.ProfileSettingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

private val TAG = "ProfileSettingViewModel"

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loginRepository: LoginRepository,
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val loginInfoKey = "loginInfo"
    private val CHECK_HANDLE_SUCCESS_CODE = "MEMBER2001"


    private val route = savedStateHandle.toRoute<ProfileSettingRoute>()
    private val loginInfo = savedStateHandle.getStateFlow(
        key = loginInfoKey,
        initialValue = Json.decodeFromString(NetworkLoginInfo.serializer(), route.loginInfoJson)
    ).value

    private val _profileSettingUiState = MutableStateFlow<ProfileSettingUiState>(ProfileSettingUiState.Idle)
    val profileSettingUiState: StateFlow<ProfileSettingUiState> = _profileSettingUiState.asStateFlow()

    fun updateProfile(profileFile: File, name: String, handle: String, message: String) {
        viewModelScope.launch {
            _profileSettingUiState.value = ProfileSettingUiState.Loading
            _profileSettingUiState.value = when (val apiResult = loginRepository.signUp(
                profileImage = profileFile,
                name = name,
                email = loginInfo.email,
                handle = handle,
                message = message,
                socialId = loginInfo.socialId,
                socialType = loginInfo.socialType
            )) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as NetworkLoginInfo

                    if (result.accessToken == null || result.refreshToken == null || result.handle == null) {
                        Log.e(
                            TAG,
                            "Server Response Error: /google/login response is missing AccessToken, RefreshToken, Handle"
                        )
                        ProfileSettingUiState.Error("AccessToken, RefreshToken, Handle is null")
                    } else {
                        // TokenManager에 AccessToken, RefreshToken, Handle 저장
                        tokenManager.saveUserData(
                            result.accessToken!!,
                            result.refreshToken!!,
                            result.handle!!
                        )

                        ProfileSettingUiState.Success
                    }
                }

                is ApiResult.Error -> ProfileSettingUiState.Error("${apiResult.code}: ${apiResult.message}")

                else -> ProfileSettingUiState.Error("Unknown Error")
            }
        }
    }

    private val _checkHandleUiState =
        MutableStateFlow<CheckHandleUiState>(CheckHandleUiState.UnChecked)
    val checkHandleUiState: StateFlow<CheckHandleUiState> = _checkHandleUiState.asStateFlow()

    fun checkHandle(handle: String) {
        viewModelScope.launch {
            _checkHandleUiState.value = CheckHandleUiState.Loading
            _checkHandleUiState.value =
                when (val apiResult = profileRepository.checkDuplicateHandle(handle)) {
                    is ApiResult.SuccessNoResult -> {
                        if (apiResult.code == CHECK_HANDLE_SUCCESS_CODE) {
                            CheckHandleUiState.Checked
                        } else {
                            CheckHandleUiState.Error("중복되는 아이디입니다.")
                        }
                    }

                    is ApiResult.Error -> CheckHandleUiState.Error("${apiResult.code}: ${apiResult.message}")

                    else -> CheckHandleUiState.Error("Unknown Error")
                }
        }
    }

    fun resetCheckHandleUiState() {
        _checkHandleUiState.value = CheckHandleUiState.UnChecked
    }
}

sealed class CheckHandleUiState {
    data object UnChecked : CheckHandleUiState()
    data object Loading : CheckHandleUiState()
    data object Checked : CheckHandleUiState()
    data class Error(val message: String) : CheckHandleUiState()
}

sealed class ProfileSettingUiState {
    data object Idle : ProfileSettingUiState()
    data object Loading : ProfileSettingUiState()
    data object Success : ProfileSettingUiState()
    data class Error(val message: String) : ProfileSettingUiState()
}
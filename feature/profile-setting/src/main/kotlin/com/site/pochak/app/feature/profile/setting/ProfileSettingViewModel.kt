package com.site.pochak.app.feature.profile.setting

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.domain.CheckHandleUseCase
import com.site.pochak.app.core.domain.SignUpUseCase
import com.site.pochak.app.core.domain.UpdateProfileUseCase
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkProfile
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.feature.profile.setting.navigation.ProfileSettingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

private val TAG = "ProfileSettingViewModel"

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val checkHandleUseCase: CheckHandleUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {
    private val loginInfoKey = "loginInfo"
    private val profileInfoKey = "profileInfo"

    private val route = savedStateHandle.toRoute<ProfileSettingRoute>()
    private val loginInfo = savedStateHandle.getStateFlow(
        key = loginInfoKey,
        initialValue = route.loginInfoJson
    )
    private val profileInfo = savedStateHandle.getStateFlow(
        key = profileInfoKey,
        initialValue = route.profileInfoJson
    )

    val uiState: StateFlow<ProfileSettingUiState> = combine(
        loginInfo,
        profileInfo
    ) { loginInfo, profileInfo ->
        if (loginInfo != null) {
            ProfileSettingUiState.SignUp(
                Json.decodeFromString(
                    NetworkLoginInfo.serializer(),
                    loginInfo
                )
            )
        } else if (profileInfo != null) {
            ProfileSettingUiState.UpdateProfile(
                Json.decodeFromString(
                    NetworkProfile.serializer(),
                    profileInfo
                )
            )
        } else {
            ProfileSettingUiState.Error
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileSettingUiState.Loading
        )

    private val _checkHandleUiState =
        MutableStateFlow<CheckHandleState>(CheckHandleState.UnChecked)
    val checkHandleState: StateFlow<CheckHandleState> = _checkHandleUiState.asStateFlow()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState.asStateFlow()

    fun checkHandle(handle: String) {
        viewModelScope.launch {
            _checkHandleUiState.value = CheckHandleState.Loading
            checkHandleUseCase.invoke(handle)
                .onStart { _checkHandleUiState.value = CheckHandleState.Loading }
                .collect {
                    _checkHandleUiState.value = if (it) {
                        CheckHandleState.Success
                    } else {
                        CheckHandleState.Error("중복되는 아이디입니다.")
                    }
                }
        }
    }

    fun resetCheckHandleState() {
        _checkHandleUiState.value = CheckHandleState.UnChecked
    }

    fun signUp(profileImage: File, name: String, handle: String, message: String) {
        viewModelScope.launch {
            val uiState = uiState.first()
            if (uiState is ProfileSettingUiState.SignUp) {
                signUpUseCase.invoke(
                    profileImage = profileImage,
                    name = name,
                    handle = handle,
                    message = message,
                    loginInfo = uiState.loginInfo
                )
                    .onStart { _signUpState.value = SignUpState.Loading }
                    .collect {
                        _signUpState.value = if (it) {
                            SignUpState.Success
                        } else {
                            SignUpState.Error
                        }
                    }
            }
        }
    }

    fun updateProfile(profileImage: File, name: String, handle: String, message: String) {
        viewModelScope.launch {
            val uiState = uiState.first()
            if (uiState is ProfileSettingUiState.UpdateProfile) {
                updateProfileUseCase.invoke(
                    profileImage = profileImage,
                    name = name,
                    handle = handle,
                    message = message
                )
                    .onStart { _updateProfileState.value = UpdateProfileState.Loading }
                    .collect {
                        _updateProfileState.value = if (it) {
                            UpdateProfileState.Success
                        } else {
                            UpdateProfileState.Error
                        }
                    }
            }
        }
    }
}

sealed interface ProfileSettingUiState {
    data object Loading : ProfileSettingUiState
    data class SignUp(val loginInfo: NetworkLoginInfo) : ProfileSettingUiState
    data class UpdateProfile(val profileInfo: NetworkProfile) : ProfileSettingUiState
    data object Error : ProfileSettingUiState
}

sealed interface CheckHandleState {
    data object UnChecked : CheckHandleState
    data object Loading : CheckHandleState
    data object Success : CheckHandleState
    data class Error(val message: String) : CheckHandleState
}

sealed interface SignUpState {
    data object Idle : SignUpState
    data object Loading : SignUpState
    data object Success : SignUpState
    data object Error : SignUpState
}

sealed interface UpdateProfileState {
    data object Idle : UpdateProfileState
    data object Loading : UpdateProfileState
    data object Success : UpdateProfileState
    data object Error : UpdateProfileState
}
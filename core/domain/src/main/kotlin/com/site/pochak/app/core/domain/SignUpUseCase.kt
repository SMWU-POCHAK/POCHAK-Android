package com.site.pochak.app.core.domain

import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenManager: TokenManager,
) {
    operator fun invoke(
        profileImage: File,
        name: String,
        handle: String,
        message: String,
        loginInfo: NetworkLoginInfo
    ): Flow<Boolean> = flow {
        when (val apiResult = loginRepository.signUp(
            profileImage = profileImage,
            name = name,
            handle = handle,
            email = loginInfo.email,
            message = message,
            socialId = loginInfo.socialId,
            socialType = loginInfo.socialType
        )) {
            is ApiResult.Success<*> -> {
                val result = apiResult.result as NetworkLoginInfo

                tokenManager.saveUserData(
                    userHandle = result.handle!!,
                    accessToken = result.accessToken!!,
                    refreshToken = result.refreshToken!!
                )

                emit(true)
            }
            else -> {
                emit(false)
            }
        }
    }
}
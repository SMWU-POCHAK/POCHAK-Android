package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.data.fileToMultiPartBody
import com.site.pochak.app.core.network.service.LoginService
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.core.network.utils.ApiResultHandler
import java.io.File
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun googleLogin(accessToken: String) =
        ApiResultHandler.handleResult {
            loginService.googleLogin(accessToken)
        }


    override suspend fun signUp(
        profileImage: File,
        name: String,
        email: String,
        handle: String,
        message: String,
        socialId: String,
        socialType: String,
        socialRefreshToken: String?
    ): ApiResult {
        val multipartBody = fileToMultiPartBody(profileImage, "ProfileImage")

        return ApiResultHandler.handleResult {
            loginService.signUp(
                profileImage = multipartBody,
                name = name,
                email = email,
                handle = handle,
                message = message,
                socialId = socialId,
                socialType = socialType,
                socialRefreshToken = socialRefreshToken
            )
        }
    }

    override suspend fun logout() =
        ApiResultHandler.handleResult {
            loginService.logout()
        }

    override suspend fun signout() =
        ApiResultHandler.handleResult {
            loginService.signout()
        }

}
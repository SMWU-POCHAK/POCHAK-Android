package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.service.LoginService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {

    override suspend fun googleLogin(accessToken: String): NetworkResponse<NetworkLoginInfo> =
        loginService.googleLogin(accessToken)


    override suspend fun signUp(
        profileImage: File,
        name: String,
        email: String,
        handle: String,
        message: String,
        socialId: String,
        socialType: String,
        socialRefreshToken: String?
    ): NetworkResponse<NetworkLoginInfo> {
        val profileImageBody = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            profileImage
        )

        val multiPartBody = MultipartBody.Part.createFormData(
            "profileImage",
            profileImage.name,
            profileImageBody
        )

        return loginService.signUp(
            profileImage = multiPartBody,
            name = name,
            email = email,
            handle = handle,
            message = message,
            socialId = socialId,
            socialType = socialType,
            socialRefreshToken = socialRefreshToken
        )
    }

    override suspend fun logout(): NetworkResponse<Unit> =
        loginService.logout()

    override suspend fun signout(): NetworkResponse<Unit> =
        loginService.signout()

}
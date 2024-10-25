package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse
import java.io.File

interface LoginRepository {

    suspend fun googleLogin(accessToken: String): NetworkResponse<NetworkLoginInfo>

    suspend fun signUp(
        profileImage: File,
        name: String,
        email: String,
        handle: String,
        message: String,
        socialId: String,
        socialType: String,
        socialRefreshToken: String? = null
    ): NetworkResponse<NetworkLoginInfo>

    suspend fun logout(): NetworkResponse<Unit>

    suspend fun signout(): NetworkResponse<Unit>

}
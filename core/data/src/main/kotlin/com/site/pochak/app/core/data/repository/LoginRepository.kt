package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.utils.ApiResult
import java.io.File

interface LoginRepository {
    /**
     * @return: [NetworkLoginInfo]
     */
    suspend fun googleLogin(accessToken: String): ApiResult

    /**
     * @return: [NetworkLoginInfo]
     */
    suspend fun signUp(
        profileImage: File,
        name: String,
        email: String,
        handle: String,
        message: String,
        socialId: String,
        socialType: String,
        socialRefreshToken: String? = null
    ): ApiResult

    suspend fun logout(): ApiResult

    suspend fun signout(): ApiResult

}
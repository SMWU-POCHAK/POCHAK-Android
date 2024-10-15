package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.UserInfo

interface LoginRepository {

    suspend fun googleLogin(accessToken: String): NetworkResponse<UserInfo>
}
package com.site.pochak.app.core.data.model.repository

import com.site.pochak.app.core.network.model.LoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse

interface LoginRepository {

    suspend fun googleLogin(accessToken: String): NetworkResponse<LoginInfo>
}
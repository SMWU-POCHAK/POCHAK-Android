package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.LoginInfo
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.service.LoginService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) {

    suspend fun googleLogin(accessToken: String) =
        loginService.googleLogin(accessToken)

}
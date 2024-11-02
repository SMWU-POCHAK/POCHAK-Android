package com.site.pochak.app.core.domain

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.site.pochak.app.core.data.repository.LoginRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "GoogleLoginUseCase"

class GoogleLoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(account: GoogleSignInAccount? = null): Flow<GoogleLoginState> = flow {
        // i) 인자가 있는 경우: 유저가 구글 로그인을 통해 접속한 경우
        // ii) 인자가 없는 경우: 유저가 이미 로그인한 상태에서 앱을 재시작한 경우
        val accountName = account?.email ?: GoogleSignIn.getLastSignedInAccount(context)?.email

        // getToken() 함수는 IO 스레드에서 실행되어야 한다.
        val accessToken = withContext(Dispatchers.IO) {
            try {
                val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile"
                accountName?.let { GoogleAuthUtil.getToken(context, accountName, scope) }
            } catch (e: Exception) {
                Log.e(TAG, "Google getAccess Token Error: $e")
                null
            }
        }

        if (accessToken == null) {
            Log.e(TAG, "Google Login Error: Access Token is null")
            emit(GoogleLoginState.LoginFailed)
            return@flow
        }

        when (val apiResult = loginRepository.googleLogin(accessToken)) {
            is ApiResult.Success<*> -> {
                val result = apiResult.result as NetworkLoginInfo

                if (result.isNewMember) {
                    emit(GoogleLoginState.SignUp(result))
                } else {
                    // 서버 응답 오류: 필수 데이터가 누락된 경우
                    if (result.accessToken == null || result.refreshToken == null || result.handle == null) {
                        Log.e(TAG, "Server Login Error: Missing data: $result")

                        emit(GoogleLoginState.LoginFailed)
                        return@flow
                    }

                    tokenManager.saveUserData(
                        result.accessToken!!,
                        result.refreshToken!!,
                        result.handle!!
                    )

                    emit(GoogleLoginState.LoginSuccess)
                }
            }

            else -> emit(GoogleLoginState.LoginFailed)
        }
    }
}

sealed interface GoogleLoginState {
    data object Loading : GoogleLoginState
    data object LoginSuccess : GoogleLoginState
    data object LoginFailed : GoogleLoginState
    data class SignUp(val loginInfo: NetworkLoginInfo) : GoogleLoginState
}
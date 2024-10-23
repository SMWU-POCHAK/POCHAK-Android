package com.site.pochak.app.core.network.token

import android.util.Log
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.BuildConfig
import com.site.pochak.app.core.network.NoAuth
import com.site.pochak.app.core.network.service.TokenService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        request.tag(Invocation::class.java)?.method()?.getAnnotation(NoAuth::class.java)?.let {
            return chain.proceed(request)
        }

        val accessToken = runBlocking {
            tokenManager.getAccessToken().firstOrNull()
        }

        if (accessToken.isNullOrEmpty()) {
            return chain.proceed(request)
        }

        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            val refreshToken = runBlocking {
                tokenManager.getRefreshToken().firstOrNull()
            }

            // 리프레시 토큰으로 새 액세스 토큰 요청
            if (!refreshToken.isNullOrEmpty()) {
                val newAccessToken = refreshAccessToken(accessToken, refreshToken)

                if (newAccessToken != null) {
                    // 새 액세스 토큰을 저장하고, 다시 요청
                    runBlocking {
                        tokenManager.saveAccessToken(newAccessToken)
                    }

                    val retryRequest = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    return chain.proceed(retryRequest)
                }
                else {
                    Log.e("AuthInterceptor", "Failed to refresh access token")
                }
            }
        }

        return response
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BACKEND_BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(TokenService::class.java)

    private fun refreshAccessToken(accessToken: String, refreshToken: String): String? {
        val headers = mapOf(
            "Authorization" to "Bearer $refreshToken",
            "RefreshToken" to "Bearer $refreshToken"
        )

        return runBlocking {
            val response = retrofit.refreshToken(headers).execute().body()

            response?.result?.accessToken
        }
    }
}
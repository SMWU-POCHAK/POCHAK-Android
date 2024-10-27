package com.site.pochak.app.core.network.utils

import android.util.Log
import com.site.pochak.app.core.network.model.NetworkResponse

object ApiResultHandler {
    suspend fun <T> handleResult(
        apiCall: suspend () -> NetworkResponse<T>
    ): ApiResult {
        return try {
            val response = apiCall()

            if (response.isSuccess) {
                if (response.result != null) {
                    ApiResult.Success(
                        code = response.code,
                        result = response.result,
                    )
                } else {
                    ApiResult.SuccessNoResult(
                        code = response.code,
                    )
                }
            } else {
                ApiResult.Error(
                    code = response.code,
                    message = response.message,
                )
            }
        } catch (e: Exception) {
            Log.e("ApiResultHandler", "handleResult: $e")
            ApiResult.UnknownError
        }
    }
}

sealed interface ApiResult {
    data class Success<T : Any>(
        val code: String,
        val result: T,
    ) : ApiResult

    data class SuccessNoResult(
        val code: String,
    ) : ApiResult

    data class Error(
        val code: String,
        val message: String,
    ) : ApiResult

    data object UnknownError : ApiResult
}
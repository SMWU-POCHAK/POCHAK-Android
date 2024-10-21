package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

/**
 *  기본 네트워크 응답 모델
 */
@Serializable
data class NetworkResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T,
)

package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T,
)

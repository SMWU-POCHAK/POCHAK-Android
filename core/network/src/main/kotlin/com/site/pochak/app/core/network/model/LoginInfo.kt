package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

/**
 *  /google/login/{accessToken} 가져올 때 [NewsResource]의 네트워크 표현
 */
@Serializable
data class LoginInfo(
    val id: Int?,
    val socialId: String,
    val name: String,
    val email: String,
    val handle: String?,
    val socialType: String,
    val accessToken: String?,
    val refreshToken: String?,
    val isNewMember: Boolean,
)
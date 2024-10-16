package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

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
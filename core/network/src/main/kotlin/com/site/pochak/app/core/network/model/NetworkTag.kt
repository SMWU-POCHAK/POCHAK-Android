package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkTag(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String
)

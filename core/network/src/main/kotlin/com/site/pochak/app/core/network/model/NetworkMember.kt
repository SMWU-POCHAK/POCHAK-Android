package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMember(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String,
    val isFollow: Boolean?
)

@Serializable
data class NetworkMemberLike(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String,
    val follow: Boolean
)
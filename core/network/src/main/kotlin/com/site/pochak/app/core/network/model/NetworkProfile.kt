package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkProfile(
    val handle: String,
    val profileImage: String,
    val name: String,
    val message: String,
    val totalPostNum: Int,
    val followerCount: Int,
    val followingCount: Int,
    val isFollow: Boolean?,
)

@Serializable
data class NetworkProfileResult(
    val name: String,
    val handle: String,
    val message: String,
    val profileImage: String,
)
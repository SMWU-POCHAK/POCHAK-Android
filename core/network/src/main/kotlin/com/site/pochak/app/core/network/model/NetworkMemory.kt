package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMemory(
    val handle: String,
    val loginMemberProfileImage: String,
    val memberProfileImage: String,
    val followDate: String,
    val followedDate: String,
    val followDay: Int,
    val pochakCount: Int,
    val bondedCount: Int,
    val pochakedCount: Int,
    val firstPochaked: NetworkPost,
    val firstPochak: NetworkPost,
    val firstBonded: NetworkPost,
    val latestPost: NetworkPost
)
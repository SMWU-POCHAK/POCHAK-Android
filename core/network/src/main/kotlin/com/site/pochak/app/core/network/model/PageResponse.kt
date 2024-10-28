package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageInfo(
    val lastPage: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int
)

@Serializable
data class AlarmPageResponse(
    val pageInfo: NetworkPageInfo,
    val alarmList: List<NetworkAlarm>
)

@Serializable
data class BlockPageResponse(
    val pageInfo: NetworkPageInfo,
    val blockList: List<NetworkMember>
)

@Serializable
data class CommentPageResponse(
    val parentCommentPageInfo: NetworkPageInfo,
    val parentCommentList: List<NetworkCommentWithChild>,
    val loginMemberProfileImage: String,
)

@Serializable
data class MemberPageResponse(
    val pageInfo: NetworkPageInfo,
    val memberList: List<NetworkMember>
)

@Serializable
data class PostPageResponse(
    val pageInfo: NetworkPageInfo,
    val postList: List<NetworkPost>
)
package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

interface PageResponse<T> {
    val pageInfo: NetworkPageInfo
    val data: List<T>
}

@Serializable
data class NetworkPageInfo(
    val lastPage: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val size: Int
)

@Serializable
data class AlarmPageResponse(
    override val pageInfo: NetworkPageInfo,
    val alarmList: List<NetworkAlarm>
) : PageResponse<NetworkAlarm> {
    override val data: List<NetworkAlarm>
        get() = alarmList
}

@Serializable
data class BlockPageResponse(
    override val pageInfo: NetworkPageInfo,
    val blockList: List<NetworkMember>
) : PageResponse<NetworkMember> {
    override val data: List<NetworkMember>
        get() = blockList
}

@Serializable
data class CommentPageResponse(
    val parentCommentPageInfo: NetworkPageInfo,
    val parentCommentList: List<NetworkCommentWithChild>,
    val loginMemberProfileImage: String,
) : PageResponse<NetworkCommentWithChild> {
    override val pageInfo: NetworkPageInfo
        get() = parentCommentPageInfo
    override val data: List<NetworkCommentWithChild>
        get() = parentCommentList
}

@Serializable
data class MemberPageResponse(
    override val pageInfo: NetworkPageInfo,
    val memberList: List<NetworkMember>
) : PageResponse<NetworkMember> {
    override val data: List<NetworkMember>
        get() = memberList
}

@Serializable
data class PostPageResponse(
    override val pageInfo: NetworkPageInfo,
    val postList: List<NetworkPost>
) : PageResponse<NetworkPost> {
    override val data: List<NetworkPost>
        get() = postList
}
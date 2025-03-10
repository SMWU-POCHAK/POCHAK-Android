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
    val parentCommentList: List<ChildCommentPageResponse>,
    val loginMemberProfileImage: String,
) : PageResponse<ChildCommentPageResponse> {
    override val pageInfo: NetworkPageInfo
        get() = parentCommentPageInfo
    override val data: List<ChildCommentPageResponse>
        get() = parentCommentList
}

@Serializable
data class ChildCommentPageResponse(
    val commentId: Int,
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val createdDate: String?,
    val content: String,
    val childCommentPageInfo: NetworkPageInfo,
    val childCommentList: MutableList<NetworkComment>
) : PageResponse<NetworkComment> {
    override val pageInfo: NetworkPageInfo
        get() = childCommentPageInfo
    override val data: List<NetworkComment>
        get() = childCommentList
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
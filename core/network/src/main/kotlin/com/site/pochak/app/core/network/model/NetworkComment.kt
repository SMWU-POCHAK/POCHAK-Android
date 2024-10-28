package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkComment(
    val commentId: Int,
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val createdDate: String,
    val content: String
)

@Serializable
data class NetworkCommentWithChild(
    val commentId: Int,
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val createdDate: String,
    val content: String,
    val childCommentPageInfo: NetworkPageInfo,
    val childCommentList: List<NetworkComment>
)

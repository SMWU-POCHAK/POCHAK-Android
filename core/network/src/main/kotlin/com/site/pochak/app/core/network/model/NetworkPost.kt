package com.site.pochak.app.core.network.model

import com.site.pochak.app.core.model.data.Post
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPost(
    val postId: Int,
    val postImage: String,
    val postDate: String? = null,
)

fun NetworkPost.toModel(): Post {
    return Post(
        postId = postId,
        postImage = postImage,
        postDate = postDate,
    )
}

@Serializable
data class NetworkPostDetail(
    val ownerId: Int,
    val ownerHandle: String,
    val ownerProfileImage: String,
    val tagList: List<NetworkTag>,
    val isFollow: Boolean?,
    val postImage: String,
    val isLike: Boolean,
    val likeCount: Int,
    val caption: String,
    val recentComment: NetworkComment?
)

@Serializable
data class NetworkPostPreview(
    val ownerId: Int,
    val ownerProfileImage: String,
    val tagList: List<NetworkTag>,
    val postImage: String
)

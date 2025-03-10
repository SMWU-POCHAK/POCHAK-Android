package com.site.pochak.app.core.network.model

import com.site.pochak.app.core.model.data.Comment
import kotlinx.serialization.Serializable

@Serializable
data class NetworkComment(
    val commentId: Int,
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val createdDate: String? = null,
    val content: String
)

fun NetworkComment.toModel(): Comment {
    return Comment(
        commentId = commentId,
        memberId = memberId,
        profileImage = profileImage,
        handle = handle,
        createdDate = createdDate,
        content = content
    )
}
package com.site.pochak.app.core.model.data

data class Comment(
    val commentId: Int,
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val createdDate: String?,
    val content: String
)
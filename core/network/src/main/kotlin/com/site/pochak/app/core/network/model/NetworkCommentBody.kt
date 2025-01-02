package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkCommentBody(
    val content: String,
    val parentCommentId: Int?
)
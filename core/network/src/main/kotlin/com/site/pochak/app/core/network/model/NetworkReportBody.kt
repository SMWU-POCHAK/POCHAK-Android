package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkReportBody(
    val postId: Int,
    val reportType: String
)
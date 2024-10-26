package com.site.pochak.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAlarm(
    val alarmId: Int,
    val alarmType: String,
    val isChecked: Boolean,
    val commentId: Int,
    val commentContent: String,
    val postId: Int,
    val postImage: String,
    val memberId: Int,
    val memberHandle: String,
    val memberName: String,
    val memberProfileImage: String
)

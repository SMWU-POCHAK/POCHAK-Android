package com.site.pochak.app.core.network.model

import com.site.pochak.app.core.model.data.Alarm
import com.site.pochak.app.core.model.data.AlarmType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkAlarm(
    val alarmId: Long,
    @SerialName("alarmType") val alarmType: String,
    val isChecked: Boolean,
    val createdDate: String? = null,
    val tagId: Long? = null,
    val ownerId: Long? = null,
    val ownerHandle: String? = null,
    val ownerName: String? = null,
    val ownerProfileImage: String? = null,
    val postId: Long? = null,
    val postImage: String? = null,
    val memberId: Long? = null,
    val memberHandle: String? = null,
    val memberName: String? = null,
    val memberProfileImage: String? = null,
    val commentId: Long? = null,
    val commentContent: String? = null
)

fun NetworkAlarm.toModel(): Alarm {
    return Alarm(
        alarmId = alarmId,
        alarmType = AlarmType.valueOf(alarmType),
        isChecked = isChecked,
        createdDate = createdDate,
        tagId = tagId,
        ownerId = ownerId,
        ownerHandle = ownerHandle,
        ownerName = ownerName,
        ownerProfileImage = ownerProfileImage,
        postId = postId,
        postImage = postImage,
        memberId = memberId,
        memberHandle = memberHandle,
        memberName = memberName,
        memberProfileImage = memberProfileImage,
        commentId = commentId,
        commentContent = commentContent
    )
}

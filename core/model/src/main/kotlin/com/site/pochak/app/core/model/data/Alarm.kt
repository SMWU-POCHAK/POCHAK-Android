package com.site.pochak.app.core.model.data

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Alarm(
    val alarmId: Long,
    val alarmType: AlarmType,
    val isChecked: Boolean,
    val createdDate: String?,
    val tagId: Long?,
    val ownerId: Long?,
    val ownerHandle: String?,
    val ownerName: String?,
    val ownerProfileImage: String?,
    val postId: Long?,
    val postImage: String?,
    val memberId: Long?,
    val memberHandle: String?,
    val memberName: String?,
    val memberProfileImage: String?,
    val commentId: Long?,
    val commentContent: String?
)

fun Alarm.getDescription(): String {
    return when (this.alarmType) {
        AlarmType.OWNER_COMMENT -> "${this.memberHandle ?: ""} 님이 댓글을 달았습니다. : ${this.commentContent ?: ""}"
        AlarmType.TAGGED_COMMENT -> "내가 포착된 게시물에 ${this.memberHandle ?: ""} 님이 댓글을 달았습니다. : ${this.commentContent ?: ""}"
        AlarmType.COMMENT_REPLY -> "나의 댓글에 ${this.memberHandle ?: ""} 님이 답글을 달았습니다. : ${this.commentContent ?: ""}"
        AlarmType.FOLLOW -> "${this.memberHandle ?: ""} 님이 회원님을 팔로우하였습니다."
        AlarmType.OWNER_LIKE -> "내 게시물에 ${this.memberHandle ?: ""} 님이 좋아요를 눌렀습니다."
        AlarmType.TAGGED_LIKE -> "내가 포착된 게시물에 ${this.memberHandle ?: ""} 님이 좋아요를 눌렀습니다."
        AlarmType.TAG_APPROVAL -> "${this.ownerHandle ?: ""} 님이 회원님을 포착했습니다."
    }
}

fun Alarm.getPostImage(): String? {
    return when (this.alarmType) {
        AlarmType.TAG_APPROVAL -> this.postImage
        else -> null
    }
}

fun Alarm.getProfileImageUrl(): String? {
    return when (this.alarmType) {
        AlarmType.TAGGED_COMMENT, AlarmType.OWNER_COMMENT -> this.memberProfileImage
        AlarmType.OWNER_LIKE, AlarmType.TAGGED_LIKE, AlarmType.COMMENT_REPLY -> this.memberProfileImage
        AlarmType.FOLLOW -> this.memberProfileImage
        AlarmType.TAG_APPROVAL -> this.ownerProfileImage
    }
}

fun Alarm.getElapsedTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val fallbackFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS") // 3자리 밀리초 처리
    val createdDateTime = try {
        LocalDateTime.parse(this.createdDate, formatter)
    } catch (e: Exception) {
        try {
            LocalDateTime.parse(this.createdDate, fallbackFormatter)
        } catch (e: Exception) {
            return "알 수 없음" // 파싱 실패 시 기본 값
        }
    }

    val now = LocalDateTime.now()
    val duration = Duration.between(createdDateTime, now)

    return when {
        duration.seconds < 60 -> "${duration.seconds}초 전"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
        duration.toHours() < 24 -> "${duration.toHours()}시간 전"
        duration.toDays() < 7 -> "${duration.toDays()}일 전"
        else -> "${duration.toDays() / 7}주 전"
    }
}

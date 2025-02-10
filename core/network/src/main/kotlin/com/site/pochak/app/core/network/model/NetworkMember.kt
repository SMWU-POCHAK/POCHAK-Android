package com.site.pochak.app.core.network.model

import com.site.pochak.app.core.model.data.Member
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMember(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String,
    val isFollow: Boolean?
)

fun NetworkMember.toModel(): Member {
    return Member(
        memberId = memberId,
        profileImage = profileImage,
        handle = handle,
        name = name,
        isFollow = isFollow
    )
}

@Serializable
data class NetworkMemberLike(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String,
    val follow: Boolean
)
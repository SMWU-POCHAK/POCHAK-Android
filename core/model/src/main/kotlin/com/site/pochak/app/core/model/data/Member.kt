package com.site.pochak.app.core.model.data

data class Member(
    val memberId: Int,
    val profileImage: String,
    val handle: String,
    val name: String,
    val isFollow: Boolean?
)
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.utils.ApiResult

interface FollowRepository {
    /**
     * @return: [MemberPageResponse]
     */
    suspend fun getFollowing(handle: String, page: Int = 0): ApiResult

    /**
     * @return: [MemberPageResponse]
     */
    suspend fun getFollower(handle: String, page: Int = 0): ApiResult

    suspend fun followMember(handle: String): ApiResult

    suspend fun unfollowMember(handle: String, followerHandle: String): ApiResult

}
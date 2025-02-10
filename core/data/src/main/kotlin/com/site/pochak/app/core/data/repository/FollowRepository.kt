package com.site.pochak.app.core.data.repository

import androidx.paging.PagingData
import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow

interface FollowRepository {
    /**
     * @return: [MemberPageResponse]
     */
    fun getFollowing(handle: String, page: Int = 0): Flow<PagingData<NetworkMember>>

    /**
     * @return: [MemberPageResponse]
     */
    fun getFollower(handle: String, page: Int = 0): Flow<PagingData<NetworkMember>>

    suspend fun followMember(handle: String): ApiResult

    suspend fun unfollowMember(handle: String, followerHandle: String): ApiResult

}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.FollowService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followService: FollowService
) : FollowRepository {
    override suspend fun getFollowing(handle: String, page: Int) =
        ApiResultHandler.handleResult {
            followService.getFollowing(handle, page)
        }

    override suspend fun getFollower(handle: String, page: Int) =
        ApiResultHandler.handleResult {
            followService.getFollower(handle, page)
        }

    override suspend fun followMember(handle: String) =
        ApiResultHandler.handleResult {
            followService.followMember(handle)
        }

    override suspend fun unfollowMember(handle: String, followerHandle: String) =
        ApiResultHandler.handleResult {
            followService.unfollowMember(handle, followerHandle)
        }

}
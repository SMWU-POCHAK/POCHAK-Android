package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.LikeService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeService: LikeService
) : LikeRepository {
    override suspend fun getLikeMembers(postId: Int, page: Int) =
        ApiResultHandler.handleResult {
            likeService.getLikeMembers(postId, page)
        }

    override suspend fun postLike(postId: Int) =
        ApiResultHandler.handleResult {
            likeService.postLike(postId)
        }

}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkMemberLike
import com.site.pochak.app.core.network.utils.ApiResult

interface LikeRepository {
    /**
     * @return: [List] of [NetworkMemberLike]
     */
    suspend fun getLikeMembers(postId: Int, page: Int = 0): ApiResult

    suspend fun postLike(postId: Int): ApiResult

}
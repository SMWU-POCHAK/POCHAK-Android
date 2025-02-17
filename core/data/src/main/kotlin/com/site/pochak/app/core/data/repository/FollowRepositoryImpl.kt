package com.site.pochak.app.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.site.pochak.app.core.data.paging.ItemPagingSource
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.service.FollowService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FollowRepositoryImpl @Inject constructor(
    private val followService: FollowService
) : FollowRepository {
    override fun getFollowing(handle: String, page: Int): Flow<PagingData<NetworkMember>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = {
                ItemPagingSource<NetworkMember> { page ->
                    ApiResultHandler.handleResult {
                        followService.getFollowing(handle, page)
                    }
                }
            }
        ).flow
    }

    override fun getFollower(handle: String, page: Int): Flow<PagingData<NetworkMember>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = {
                ItemPagingSource<NetworkMember> { page ->
                    ApiResultHandler.handleResult {
                        followService.getFollower(handle, page)
                    }
                }
            }
        ).flow
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
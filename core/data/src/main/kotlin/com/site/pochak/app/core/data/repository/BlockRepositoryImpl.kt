package com.site.pochak.app.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.site.pochak.app.core.data.paging.ItemPagingSource
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.service.BlockService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(
    private val blockService: BlockService
) : BlockRepository {
    override suspend fun blockMember(handle: String) = ApiResultHandler.handleResult {
        blockService.blockMember(handle)
    }

    override fun getBlockedMembers(handle: String): Flow<PagingData<NetworkMember>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = {
                ItemPagingSource<NetworkMember> { page ->
                    ApiResultHandler.handleResult {
                        blockService.getBlockedMembers(handle, page)
                    }
                }
            }
        ).flow
    }

    override suspend fun unblockUser(handle: String, blockedMemberHandle: String) =
        ApiResultHandler.handleResult {
            blockService.unblockUser(handle, blockedMemberHandle)
        }

}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.BlockService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(
    private val blockService: BlockService
) : BlockRepository {
    override suspend fun blockMember(handle: String) = ApiResultHandler.handleResult {
        blockService.blockMember(handle)
    }

    override suspend fun getBlockedMembers(handle: String, page: Int) =
        ApiResultHandler.handleResult {
            blockService.getBlockedMembers(handle, page)
        }

    override suspend fun unblockUser(handle: String, blockedMemberHandle: String) =
        ApiResultHandler.handleResult {
            blockService.unblockUser(handle, blockedMemberHandle)
        }

}
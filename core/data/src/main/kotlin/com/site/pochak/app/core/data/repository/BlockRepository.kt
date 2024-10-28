package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.utils.ApiResult

interface BlockRepository {
    suspend fun blockMember(handle: String): ApiResult

    /**
     * @return: [MemberPageResponse]
     */
    suspend fun getBlockedMembers(handle: String, page: Int): ApiResult

    suspend fun unblockUser(handle: String, blockedMemberHandle: String): ApiResult

}
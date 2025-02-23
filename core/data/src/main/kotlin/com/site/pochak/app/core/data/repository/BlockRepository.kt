package com.site.pochak.app.core.data.repository

import androidx.paging.PagingData
import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow

interface BlockRepository {
    suspend fun blockMember(handle: String): ApiResult

    /**
     * @return: [MemberPageResponse]
     */
    fun getBlockedMembers(handle: String): Flow<PagingData<NetworkMember>>

    suspend fun unblockUser(handle: String, blockedMemberHandle: String): ApiResult

}
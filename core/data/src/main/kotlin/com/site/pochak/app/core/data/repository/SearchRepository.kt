package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.utils.ApiResult

interface SearchRepository {
    /**
     * @return: [MemberPageResponse]
     */
    suspend fun searchMembers(keyword: String, page: Int = 0): ApiResult

}
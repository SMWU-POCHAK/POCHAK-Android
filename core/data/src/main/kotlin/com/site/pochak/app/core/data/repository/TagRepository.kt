package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.utils.ApiResult

interface TagRepository {
    suspend fun approveTag(tagId: Int, isAccept: Boolean): ApiResult

}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.utils.ApiResult

interface ProfileRepository {
    suspend fun checkDuplicateHandle(handle: String): ApiResult

}
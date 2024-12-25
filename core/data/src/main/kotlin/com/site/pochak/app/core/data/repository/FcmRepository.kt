package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkFcm
import com.site.pochak.app.core.network.utils.ApiResult

interface FcmRepository {
    suspend fun registerFcmToken(
        token: NetworkFcm
    ): ApiResult

    suspend fun deleteFcmToken(): ApiResult
}


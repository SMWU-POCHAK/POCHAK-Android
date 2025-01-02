package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkFcm
import com.site.pochak.app.core.network.service.FcmService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val fcmService: FcmService
) : FcmRepository {
    override suspend fun registerFcmToken(token: NetworkFcm) =
        ApiResultHandler.handleResult {
            fcmService.registerFcmToken(token)
        }

    override suspend fun deleteFcmToken() =
        ApiResultHandler.handleResult {
            fcmService.deleteFcmToken()
        }
}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.ProfileService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) : ProfileRepository {
    override suspend fun checkDuplicateHandle(handle: String) =
        ApiResultHandler.handleResult {
            profileService.checkDuplicateHandle(handle)
        }

}
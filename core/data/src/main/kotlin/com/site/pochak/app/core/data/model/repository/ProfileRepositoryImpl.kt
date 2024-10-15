package com.site.pochak.app.core.data.model.repository

import com.site.pochak.app.core.network.service.ProfileService
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) {

    suspend fun checkDuplicateHandle(handle: String): Boolean =
        profileService.checkDuplicateHandle(handle).isSuccess
}
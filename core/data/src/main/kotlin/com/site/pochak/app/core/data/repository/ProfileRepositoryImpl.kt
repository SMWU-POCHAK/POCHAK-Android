package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.service.ProfileService
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) : ProfileRepository {

    override suspend fun checkDuplicateHandle(handle: String): NetworkResponse<Unit> =
        profileService.checkDuplicateHandle(handle)

}
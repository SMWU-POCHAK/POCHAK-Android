package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkResponse

interface ProfileRepository {

    suspend fun checkDuplicateHandle(handle: String): NetworkResponse<Unit>

}
package com.site.pochak.app.core.data.model.repository

interface ProfileRepository {

    suspend fun checkDuplicateHandle(handle: String): Boolean
}
package com.site.pochak.app.core.data.repository

interface ProfileRepository {

    suspend fun checkDuplicateHandle(handle: String): Boolean
}
package com.site.pochak.app.core.data.repository

import androidx.paging.PagingData
import com.site.pochak.app.core.network.model.NetworkPost
import com.site.pochak.app.core.network.utils.ApiResult
import java.io.File
import com.site.pochak.app.core.network.model.NetworkProfileResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    /**
     * @return: [NetworkProfile]
     */
    suspend fun getProfile(handle: String, page: Int): ApiResult

    fun getPochakedPosts(handle: String): Flow<PagingData<NetworkPost>>

    fun getPochakPosts(handle: String): Flow<PagingData<NetworkPost>>

    /**
     * @return: [NetworkProfileResult]
     */
    suspend fun updateProfile(
        handle: String,
        postImage: File,
        name: String,
        message: String
    ): ApiResult

    suspend fun checkDuplicateHandle(handle: String): ApiResult

}
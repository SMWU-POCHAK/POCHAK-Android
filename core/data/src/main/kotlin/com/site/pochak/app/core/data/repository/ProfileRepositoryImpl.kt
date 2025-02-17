package com.site.pochak.app.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.site.pochak.app.core.data.fileToMultiPartBody
import com.site.pochak.app.core.data.paging.ItemPagingSource
import com.site.pochak.app.core.network.model.NetworkPost
import com.site.pochak.app.core.network.service.ProfileService
import com.site.pochak.app.core.network.utils.ApiResult
import com.site.pochak.app.core.network.utils.ApiResultHandler
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) : ProfileRepository {
    override suspend fun getProfile(handle: String, page: Int) =
        ApiResultHandler.handleResult {
            profileService.getProfile(handle, page)
        }

    override fun getPochakedPosts(handle: String): Flow<PagingData<NetworkPost>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = { ItemPagingSource<NetworkPost> { page ->
                ApiResultHandler.handleResult {
                    profileService.getPochakedPosts(handle, page)
                }
            }}
        ).flow
    }

    override fun getPochakPosts(handle: String): Flow<PagingData<NetworkPost>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = { ItemPagingSource<NetworkPost> { page ->
                ApiResultHandler.handleResult {
                    profileService.getPochakPosts(handle, page)
                }
            }}
        ).flow
    }

    override suspend fun updateProfile(
        handle: String,
        postImage: File,
        name: String,
        message: String
    ): ApiResult {
        val multipartBody = fileToMultiPartBody(postImage, "postImage")

        return ApiResultHandler.handleResult {
            profileService.updateProfile(
                handle = handle,
                postImage = multipartBody,
                name = name,
                message = message
            )
        }
    }

    override suspend fun checkDuplicateHandle(handle: String) =
        ApiResultHandler.handleResult {
            profileService.checkDuplicateHandle(handle)
        }

}
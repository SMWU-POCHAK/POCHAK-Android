package com.site.pochak.app.core.domain

import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.NetworkProfileResult
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
) {
    operator fun invoke(
        handle: String,
        profileImage: File,
        name: String,
        message: String,
    ): Flow<Boolean> = flow {
        when (val apiResult = profileRepository.updateProfile(
            handle = handle,
            postImage = profileImage,
            name = name,
            message = message,
        )) {
            is ApiResult.Success<*> -> {
                val result = apiResult.result as NetworkProfileResult

                tokenManager.saveUserHandle(result.handle)

                emit(true)
            }

            else -> {
                emit(false)
            }
        }
    }

}
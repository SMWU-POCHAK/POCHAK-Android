package com.site.pochak.app.core.domain

import com.site.pochak.app.core.data.repository.ProfileRepository
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val CHECK_HANDLE_SUCCESS_CODE = "MEMBER2001"

class CheckHandleUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(handle: String): Flow<Boolean> = flow {
        when (val result = profileRepository.checkDuplicateHandle(handle)) {
            is ApiResult.SuccessNoResult -> {
                emit(result.code == CHECK_HANDLE_SUCCESS_CODE)
            }

            is ApiResult.Error -> {
                emit(false)
            }

            else -> {
                emit(false)
            }
        }
    }
}
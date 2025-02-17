package com.site.pochak.app.core.domain

import com.site.pochak.app.core.data.repository.FollowRepository
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val FOLLOW_SUCCESS = "COMMON200"

class FollowUseCase @Inject constructor(
    private val followRepository: FollowRepository,
) {
    operator fun invoke(handle: String): Flow<Boolean> = flow {
        when (val result = followRepository.followMember(handle)) {
            is ApiResult.SuccessNoResult -> {
                if (result.code == FOLLOW_SUCCESS) {
                    emit(true)
                } else {
                    emit(false)
                }
            }

            is ApiResult.Error -> {
                emit(false)
            }

            else -> emit(false)
        }
    }
}

sealed interface FollowState {
    data object Loading : FollowState
    data object Success : FollowState
    data class Error(val message: String) : FollowState
}
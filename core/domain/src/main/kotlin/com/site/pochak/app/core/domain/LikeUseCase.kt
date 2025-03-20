package com.site.pochak.app.core.domain

import com.site.pochak.app.core.data.repository.LikeRepository
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LIKE_SUCCESS = "LIKE2001"

class LikeUseCase @Inject constructor(
    private val likeRepository: LikeRepository
) {
    operator fun invoke(postId: Int): Flow<Boolean> = flow {
        when (val result = likeRepository.postLike(postId)) {
            is ApiResult.SuccessNoResult -> {
                if (result.code == LIKE_SUCCESS) {
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
package com.site.pochak.app.core.domain

import android.net.http.HttpException
import android.util.Log
import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val TAG = "PostUseCase"

class PostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(
        postImage: File,
        taggedMemberHandleList: List<String>,
        caption: String
    ): Flow<UploadUiState> = flow {
        emit(UploadUiState.Loading) // 업로드 시작 시 로딩 상태 방출

        try {
            val result = withContext(Dispatchers.IO) {
                postRepository.postPost(postImage, taggedMemberHandleList, caption)
            }

            // ApiResult 기반 결과 처리
            when (result) {
                is ApiResult.Success<*> -> {
                    emit(UploadUiState.Success) // 업로드 성공 시 성공 상태 방출
                }
                is ApiResult.SuccessNoResult -> {
                    emit(UploadUiState.Success) // 성공 (결과 없음)
                }
                is ApiResult.Error -> {
                    // 상태 코드가 500인지 확인
                    if (result.code == "500") {
                        emit(UploadUiState.Failed("Internal Server Error (500)"))
                    } else {
                        emit(UploadUiState.Failed("Error ${result.code}: ${result.message}"))
                    }
                }
                is ApiResult.UnknownError -> {
                    emit(UploadUiState.Failed("An unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Post Error: $e")
            emit(UploadUiState.Failed(e.message ?: "Unknown error"))
        }
    }
}


// 게시물 생성 상태를 나타내는 sealed interface
sealed interface UploadUiState {
    data object Idle : UploadUiState
    data object Loading : UploadUiState
    data object Success : UploadUiState
    data class Failed(val error: String) : UploadUiState
}

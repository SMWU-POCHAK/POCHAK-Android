package com.site.pochak.app.core.domain

import android.util.Log
import com.site.pochak.app.core.data.repository.PostRepository
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
        // 게시물 업로드를 시작할 때 Loading 상태를 방출
        emit(UploadUiState.Loading)

        try {
            // 실제 게시물 업로드 API 호출 (IO 스레드에서 실행)
            withContext(Dispatchers.IO) {
                postRepository.postPost(postImage, taggedMemberHandleList, caption)
            }
            // 업로드 성공 시 Success 상태 방출
            emit(UploadUiState.Success)
        } catch (e: Exception) {
            // 오류가 발생하면 Failed 상태 방출
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

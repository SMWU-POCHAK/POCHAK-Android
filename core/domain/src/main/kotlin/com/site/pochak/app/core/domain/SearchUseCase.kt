package com.site.pochak.app.core.domain

import android.util.Log
import com.site.pochak.app.core.data.repository.SearchRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SearchUseCase"

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val tokenManager: TokenManager
) {
    operator fun invoke(keyword: String, page: Int = 0): Flow<SearchMembersUiState> = flow {
        emit(SearchMembersUiState.Loading) // 로딩 상태 방출

        // 사용자 핸들을 IO 스레드에서 가져옴
        val userHandle = withContext(Dispatchers.IO) {
            tokenManager.getUserHandle().first() // Flow에서 첫 번째 값만 가져옴
        }

        // 검색 API 호출
        val apiResult = searchRepository.searchMembers(keyword, page)
        val state = when (apiResult) {
            is ApiResult.Success<*> -> {
                Log.d(TAG, "Success: ${apiResult.result}")
                val result = apiResult.result as MemberPageResponse
                val filteredMembers = result.memberList.filter { it.handle != userHandle }
                SearchMembersUiState.Success(filteredMembers)
            }
            is ApiResult.Error -> SearchMembersUiState.Error(apiResult.message)
            else -> SearchMembersUiState.Error("Unknown error")
        }

        emit(state) // 최종 상태 방출
    }.flowOn(Dispatchers.IO) // 전체 Flow를 IO 스레드에서 실행
}

sealed class SearchMembersUiState {
    object Empty : SearchMembersUiState()
    object Loading : SearchMembersUiState()
    data class Success(val members: List<NetworkMember>) : SearchMembersUiState()
    data class Error(val message: String) : SearchMembersUiState()
}
package com.site.pochak.app.feature.camera

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.SearchRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.network.model.MemberPageResponse
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val TAG = "UploadViewModel"

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _searchMembersUiState = MutableStateFlow<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: StateFlow<SearchMembersUiState> = _searchMembersUiState.asStateFlow()

    fun searchMembers(keyword: String, page: Int = 0) {
        viewModelScope.launch {
            _searchMembersUiState.value = SearchMembersUiState.Loading
            // 사용자 핸들을 비동기적으로 가져옴
            val userHandle = withContext(Dispatchers.IO) {
                // collect the first value from the flow
                tokenManager.getUserHandle().first() // Collect the first value emitted
            }

            _searchMembersUiState.value = when (val apiResult = searchRepository.searchMembers(keyword, page)) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as MemberPageResponse
                    Log.e(
                        TAG,
                        "searchMembers: ${result.memberList} members found for keyword: $keyword"
                    )
                    SearchMembersUiState.Success(result.memberList.filter { it.handle != userHandle })
                }
                is ApiResult.Error -> SearchMembersUiState.Error(apiResult.message)

                else -> SearchMembersUiState.Error("Unknown error")

            }
        }
    }
}

sealed class SearchMembersUiState {
    object Empty : SearchMembersUiState()
    object Loading : SearchMembersUiState()
    data class Success(val members: List<NetworkMember>) : SearchMembersUiState()
    data class Error(val message: String) : SearchMembersUiState()
}

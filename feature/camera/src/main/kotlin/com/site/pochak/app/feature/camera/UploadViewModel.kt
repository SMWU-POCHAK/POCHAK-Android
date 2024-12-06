package com.site.pochak.app.feature.camera

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.domain.PostUseCase
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.domain.SearchUseCase
import com.site.pochak.app.core.domain.UploadUiState
import com.site.pochak.app.core.network.model.NetworkMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "UploadViewModel"

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val postUseCase: PostUseCase,
) : ViewModel() {

    private val _searchMembersUiState = mutableStateOf<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: State<SearchMembersUiState> = _searchMembersUiState

    // 게시물 생성 상태를 담는 상태 변수
    private val _uploadUiState = mutableStateOf<UploadUiState>(UploadUiState.Idle)
    val uploadUiState: State<UploadUiState> = _uploadUiState

    // 페이징 관련 변수들
    private var currentPage = 0  // 현재 페이지
    private var isPaging = false // 페이징 중인지 여부
    private val pageSize = 30    // 한 페이지당 항목 수

    private val _searchResults = mutableStateOf<List<NetworkMember>>(emptyList())
    val searchResults: State<List<NetworkMember>> = _searchResults

    fun searchMembers(keyword: String, page: Int = 0) {
        viewModelScope.launch {
            if (isPaging) return@launch

            isPaging = true
            _searchMembersUiState.value = SearchMembersUiState.Loading  // 로딩 상태 업데이트

            searchUseCase(keyword, page).collect { state ->
                when (state) {
                    is SearchMembersUiState.Success -> {
                        val updatedResults = if (page == 0) {
                            Log.d(TAG, "SearchMembersUiState.Success: ${state.members}")
                            state.members
                        } else {
                            _searchResults.value + state.members
                        }
                        _searchResults.value = updatedResults
                        _searchMembersUiState.value = state  // 성공 시 상태 업데이트
                    }
                    is SearchMembersUiState.Error -> {
                        _searchMembersUiState.value = state  // 에러 시 상태 업데이트
                    }
                    else -> _searchMembersUiState.value = state
                }
                isPaging = false  // 페이징 완료
            }
        }
    }

    // 검색 결과를 지우는 함수
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    // 다음 페이지 불러오기 함수
    fun loadNextPage(keyword: String) {
        if (!isPaging) {
            currentPage += 1
            searchMembers(keyword, currentPage)
        }
    }

    // 게시물 생성 함수
    fun postPost(postImage: File, taggedMemberHandleList: List<String>, caption: String) {
        viewModelScope.launch {
            postUseCase(postImage, taggedMemberHandleList, caption).collect { state ->
                // postUseCase의 상태를 _uploadUiState로 업데이트
                _uploadUiState.value = state
            }
        }
    }
}
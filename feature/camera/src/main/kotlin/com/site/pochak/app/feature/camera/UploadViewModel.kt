package com.site.pochak.app.feature.camera

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.data.repository.SearchRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.domain.UploadUiState
import com.site.pochak.app.core.domain.PostUseCase
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.domain.SearchUseCase
import com.site.pochak.app.core.network.model.MemberPageResponse
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
    private val searchUseCase: SearchUseCase,
    private val postUseCase: PostUseCase,
) : ViewModel() {

    private val _searchMembersUiState = mutableStateOf<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: State<SearchMembersUiState> = _searchMembersUiState

    // 게시물 생성 상태를 담는 상태 변수
    private val _uploadUiState = mutableStateOf<UploadUiState>(UploadUiState.Loading)
    val uploadUiState: State<UploadUiState> = _uploadUiState

    // 검색 기능 호출 함수
    fun searchMembers(keyword: String, page: Int = 0) {
        viewModelScope.launch {
            // UseCase의 결과 Flow를 collect하여 상태 업데이트
            searchUseCase(keyword, page).collect { state ->
                _searchMembersUiState.value = state
            }
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
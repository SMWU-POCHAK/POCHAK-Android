package com.site.pochak.app.feature.camera

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.site.pochak.app.core.data.repository.FollowRepository
import com.site.pochak.app.core.datastore.TokenManager
import com.site.pochak.app.core.domain.FollowUseCase
import com.site.pochak.app.core.domain.PostUseCase
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.domain.SearchUseCase
import com.site.pochak.app.core.domain.UploadUiState
import com.site.pochak.app.core.model.data.Member
import com.site.pochak.app.core.network.model.NetworkMember
import com.site.pochak.app.core.network.model.toModel
import com.site.pochak.app.feature.profile.navigation.FollowRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val TAG = "UploadViewModel"

@HiltViewModel
class UploadViewModel @Inject constructor(
    saveStateHandle: SavedStateHandle,
    followRepository: FollowRepository,
    private val searchUseCase: SearchUseCase,
    private val postUseCase: PostUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userHandle = MutableStateFlow<String?>(null)
    val userHandle: StateFlow<String?> = _userHandle

    init {
        viewModelScope.launch {
            val handle = withContext(Dispatchers.IO) {
                tokenManager.getUserHandle().first()
            }
            _userHandle.value = handle
        }
    }

    suspend fun getUserHandle(): String {
        return withContext(Dispatchers.IO) {
            tokenManager.getUserHandle().toString()
        }
    }

    private val _searchMembersUiState =
        mutableStateOf<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: State<SearchMembersUiState> = _searchMembersUiState

    // 게시물 생성 상태를 담는 상태 변수
    private val _uploadUiState = mutableStateOf<UploadUiState>(UploadUiState.Idle)
    val uploadUiState: State<UploadUiState> = _uploadUiState

    // 페이징 관련 변수들
    private var currentPage = 0  // 현재 페이지
    private var isPaging = false // 페이징 중인지 여부
    private val pageSize = 30    // 한 페이지당 항목 수

    private val _searchResults = mutableStateOf<List<Member>>(emptyList())
    val searchResults: State<List<Member>> = _searchResults

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
                            state.members.map { it.toModel() }
                        } else {
                            _searchResults.value + state.members.map { it.toModel() }
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

    // 태그 아이디 검색 전 팔로잉 리스트
    val followingList: StateFlow<PagingData<Member>> = userHandle
        .filterNotNull()  // null이 아닌 값만 통과
        .flatMapLatest { handle ->
            followRepository.getFollowing(handle).map { pagingData ->
                pagingData.map { networkMember ->
                    networkMember.toModel()
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

}
package com.site.pochak.app.feature.post

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.domain.SearchUseCase
import com.site.pochak.app.core.model.data.RecentSearch
import com.site.pochak.app.core.model.data.dao.RecentSearchDao
import com.site.pochak.app.core.network.model.NetworkMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

private const val TAG = "SearchHistoryViewModel"

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val recentSearchDao: RecentSearchDao
) : ViewModel() {
    // 최근 검색어 LiveData
    private val _recentSearches = MutableLiveData<List<RecentSearch>>()
    val recentSearches: LiveData<List<RecentSearch>> = _recentSearches

    private val _searchMembersUiState = MutableStateFlow<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: StateFlow<SearchMembersUiState> = _searchMembersUiState

    private val _searchResults = mutableStateOf<List<NetworkMember>>(emptyList())
    val searchResults: State<List<NetworkMember>> = _searchResults

    init {
        loadRecentSearches() // 초기화 시 최근 검색어 불러오기
    }

    // 최근 검색어 불러오기
    private fun loadRecentSearches() {
        viewModelScope.launch {
            _recentSearches.value = recentSearchDao.getAllRecentSearches() // DAO를 통해 데이터 로드
        }
    }

    // 최근 검색어 추가
    fun addSearchItem(handle: String, name: String, profileImageUrl: String) {
        val newSearch = RecentSearch(
            id = UUID.randomUUID().toString(),
            handle = handle,
            name = name,
            profileImage = profileImageUrl,
            timestamp = Date().time
        )

        viewModelScope.launch {
            recentSearchDao.insertRecentSearch(newSearch) // DAO를 통해 데이터 삽입
            loadRecentSearches() // 업데이트된 데이터 로드
        }
    }

    // 특정 검색어 삭제
    fun removeSearchItem(itemId: String) {
        viewModelScope.launch {
            recentSearchDao.deleteRecentSearchById(itemId) // DAO를 통해 데이터 삭제
            loadRecentSearches() // 업데이트된 데이터 로드
        }
    }

    // 전체 삭제
    fun clearAllSearches() {
        viewModelScope.launch {
            recentSearchDao.deleteAllRecentSearches() // DAO를 통해 데이터 전부 삭제
            loadRecentSearches() // 업데이트된 데이터 로드
        }
    }

    fun searchMembers(keyword: String, page: Int = 0) {
        viewModelScope.launch {
            _searchMembersUiState.value = SearchMembersUiState.Loading  // 로딩 상태
            searchUseCase(keyword, page).collect { state ->
                _searchMembersUiState.value = state  // 결과 상태 업데이트
            }
        }
    }

    // 검색 결과를 지우는 함수
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}
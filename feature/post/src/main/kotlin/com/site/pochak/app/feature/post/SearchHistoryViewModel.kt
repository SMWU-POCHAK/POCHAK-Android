package com.site.pochak.app.feature.post

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

    private val _currentKeyword = MutableStateFlow("")
    val currentKeyword: StateFlow<String> = _currentKeyword

    private val _searchMembersUiState = MutableStateFlow<SearchMembersUiState>(SearchMembersUiState.Empty)
    val searchMembersUiState: StateFlow<SearchMembersUiState> = _searchMembersUiState

    private val _searchResults = MutableStateFlow<List<NetworkMember>>(emptyList())
    val searchResults: StateFlow<List<NetworkMember>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var currentPage = 0
    private var isLastPage = false

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
        viewModelScope.launch {
            val existingSearch = recentSearchDao.getRecentSearchByHandle(handle) // 기존 검색 확인
            if (existingSearch != null) {
                // 기존 항목의 timestamp 갱신
                val updatedSearch = existingSearch.copy(timestamp = Date().time)
                recentSearchDao.insertRecentSearch(updatedSearch)
            } else {
                // 새로운 검색어 추가
                val newSearch = RecentSearch(
                    id = UUID.randomUUID().toString(),
                    handle = handle,
                    name = name,
                    profileImage = profileImageUrl,
                    timestamp = Date().time
                )
                recentSearchDao.insertRecentSearch(newSearch)
            }
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

    fun searchMembers(isRefresh: Boolean = true) {
        if (isRefresh) {
            currentPage = 0
            isLastPage = false
            _searchResults.value = emptyList() // 기존 결과 초기화
        }

        if (isLastPage) return

        _isLoading.value = true
        _isRefreshing.value = isRefresh

        viewModelScope.launch {
            searchUseCase(currentKeyword.value, currentPage).collect { state ->
                when (state) {
                    is SearchMembersUiState.Success -> {
                        val updatedResults = if (isRefresh) {
                            state.members
                        } else {
                            _searchResults.value + state.members
                        }
                        _searchResults.value = updatedResults
                        _searchMembersUiState.value = SearchMembersUiState.Success(updatedResults)

                        // 페이징 처리
                        currentPage++
                        isLastPage = state.members.isEmpty() // 결과가 없으면 마지막 페이지로 설정
                    }
                    is SearchMembersUiState.Error -> {
                        _searchMembersUiState.value = state // 에러 상태 업데이트
                    }
                    else -> {
                        _searchMembersUiState.value = SearchMembersUiState.Empty
                    }
                }

                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    // 검색 결과를 지우는 함수
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun updateKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }
}

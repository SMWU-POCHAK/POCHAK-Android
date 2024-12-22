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

    /**
     * 검색 기록 및 멤버 검색 작업을 관리하는 ViewModel
     */
    private fun loadRecentSearches() = viewModelScope.launch {
        _recentSearches.value = recentSearchDao.getAllRecentSearches()
    }

    /**
     * 새로운 검색 항목을 최근 검색 목록에 추가하거나, 이미 존재하는 경우 타임스탬프 업데이트.
     *
     * @param handle 검색된 멤버의 고유 핸들.
     * @param name 검색된 멤버의 이름.
     * @param profileImageUrl 검색된 멤버의 프로필 이미지 URL.
     */
    fun addSearchItem(handle: String, name: String, profileImageUrl: String) = viewModelScope.launch {
        val existingSearch = recentSearchDao.getRecentSearchByHandle(handle)
        val searchToInsert = existingSearch?.copy(timestamp = Date().time) ?: RecentSearch(
            id = UUID.randomUUID().toString(),
            handle = handle,
            name = name,
            profileImage = profileImageUrl,
            timestamp = Date().time
        )
        recentSearchDao.insertRecentSearch(searchToInsert)
        loadRecentSearches()
    }

    /**
     * 특정 검색 항목을 최근 검색 목록에서 삭제.
     *
     * @param itemId 삭제할 검색 항목의 고유 ID.
     */
    fun removeSearchItem(itemId: String) = viewModelScope.launch {
        recentSearchDao.deleteRecentSearchById(itemId)
        loadRecentSearches()
    }

    /**
     * 최근 검색 목록에서 모든 검색 항목 삭제.
     */
    fun clearAllSearches() = viewModelScope.launch {
        recentSearchDao.deleteAllRecentSearches()
        loadRecentSearches()
    }

    /**
     * 현재 키워드를 기반으로 멤버를 검색하며, 선택적으로 새로고침 및 페이징.
     *
     * @param isRefresh 검색 결과를 새로고침할지 여부. 기본값은 true.
     */
    fun searchMembers(isRefresh: Boolean = true) {
        if (isRefresh) resetSearchState()
        if (isLastPage) return

        _isLoading.value = true
        _isRefreshing.value = isRefresh

        viewModelScope.launch {
            searchUseCase(currentKeyword.value, currentPage).collect { state ->
                handleSearchState(state, isRefresh)
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 검색 상태를 초기화하고 페이징 및 검색 결과 리셋.
     */
    private fun resetSearchState() {
        currentPage = 0
        isLastPage = false
        _searchResults.value = emptyList()
    }

    /**
     * 멤버 검색 작업의 상태 처리.
     * 검색 결과를 업데이트하고 페이징 관리.
     *
     * @param state 멤버 검색 작업의 현재 상태.
     * @param isRefresh 검색 결과가 새로고침 중인지 여부.
     */
    private fun handleSearchState(state: SearchMembersUiState, isRefresh: Boolean) {
        when (state) {
            is SearchMembersUiState.Success -> {
                val updatedResults = if (isRefresh) state.members else _searchResults.value + state.members
                _searchResults.value = updatedResults
                _searchMembersUiState.value = SearchMembersUiState.Success(updatedResults)
                currentPage++
                isLastPage = state.members.isEmpty()
            }
            is SearchMembersUiState.Error -> {
                _searchMembersUiState.value = state
            }
            else -> {
                _searchMembersUiState.value = SearchMembersUiState.Empty
            }
        }
    }

    /**
     * 현재 검색 결과 초기화.
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    /**
     * 현재 검색 키워드 업데이트.
     *
     * @param keyword 새로운 검색 키워드.
     */
    fun updateKeyword(keyword: String) {
        _currentKeyword.value = keyword
    }
}

package com.site.pochak.app.core.data.util

import com.site.pochak.app.core.data.repository.PostRepository
import com.site.pochak.app.core.network.model.PageResponse
import com.site.pochak.app.core.network.utils.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * PageResponseManager is a class that manages the [PageResponse] data.
 *
 * @param T: [PageResponse.data] type
 * @property fetchPage: function to fetch the page data.(ex. [PostRepository.getHomePosts])
 * @property scope: CoroutineScope for run the fetchPage function.(ex. viewModelScope)
 */
class PageResponseManager<T>(
    private val fetchPage: suspend (Int) -> ApiResult,
    private val scope: CoroutineScope
) {
    private val _data = MutableStateFlow(emptyList<T>())
    val data: StateFlow<List<T>> = _data.asStateFlow()

    private var page = 0
    private var isLastPage = false

    // Load next page
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Refresh page
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadPage(true)
    }

    fun loadPage(isRefresh: Boolean) {
        if (isLastPage && !isRefresh) return

        if (isRefresh) {
            _isRefreshing.value = true
            isLastPage = false
            page = 0
            _data.update { emptyList() }
        }

        scope.launch {
            _isLoading.value = true

            when (val apiResult = fetchPage(page)) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as PageResponse<T>

                    _data.update { it + result.data }
                    page++

                    isLastPage = result.pageInfo.lastPage
                }

                else -> {
                    // Handle error
                }
            }

            _isRefreshing.value = false
            _isLoading.value = false
        }
    }
}

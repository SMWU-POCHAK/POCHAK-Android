package com.site.pochak.app.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.site.pochak.app.core.network.model.PageResponse
import com.site.pochak.app.core.network.utils.ApiResult

class ItemPagingSource<T: Any>(
    private val apiCall: suspend (Int) -> ApiResult
): PagingSource<Int, T>() {
    private val DEFAULT_PAGE = 0

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        // Return the first page key (default = 0)
        return DEFAULT_PAGE
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: DEFAULT_PAGE
            when (val apiResult = apiCall(page)) {
                is ApiResult.Success<*> -> {
                    val result = apiResult.result as PageResponse<T>

                    LoadResult.Page(
                        data = result.data,
                        prevKey = if (page == DEFAULT_PAGE) null else page - 1,
                        nextKey = if (result.pageInfo.lastPage) null else page + 1
                    )
                }
                else -> LoadResult.Error(Exception("ApiResult is not Success"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}
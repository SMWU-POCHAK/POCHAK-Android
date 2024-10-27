package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.SearchService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SearchService
) : SearchRepository {
    override suspend fun searchMembers(keyword: String, page: Int) =
        ApiResultHandler.handleResult {
            searchService.searchMembers(keyword, page)
        }

}
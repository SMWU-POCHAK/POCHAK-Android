package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.utils.ApiResult

interface ReportRepository {
    suspend fun postReport(postId: Int, reportType: String): ApiResult

}
package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.NetworkResponse
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Report API
 *
 * @POST /api/v2/reports: 신고 API
 *
 */
interface ReportService {

    @POST(value = "api/v2/reports")
    fun postReport(
        @Query(value = "postId") postId: Int,
        @Query(value = "reportType") reportType: String
    ): NetworkResponse<Unit>

}
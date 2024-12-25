package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.NetworkReportBody
import com.site.pochak.app.core.network.service.ReportService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService
) : ReportRepository {
    override suspend fun postReport(postId: Int, reportType: String) =
        ApiResultHandler.handleResult {
            reportService.postReport(NetworkReportBody(postId, reportType))
        }

}
package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.service.AlarmService
import com.site.pochak.app.core.network.utils.ApiResultHandler
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmService: AlarmService
) : AlarmRepository {
    override suspend fun getAllAlarms(page: Int) =
        ApiResultHandler.handleResult {
            alarmService.getAllAlarms(page)
        }

    override suspend fun getPreviewPost(alarmId: Int) =
        ApiResultHandler.handleResult {
            alarmService.getPreviewPost(alarmId)
        }

    override suspend fun checkAlarm(alarmId: Int) =
        ApiResultHandler.handleResult {
            alarmService.checkAlarm(alarmId)
        }

}
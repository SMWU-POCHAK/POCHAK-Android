package com.site.pochak.app.core.data.repository

import com.site.pochak.app.core.network.model.AlarmPageResponse
import com.site.pochak.app.core.network.model.NetworkPostPreview
import com.site.pochak.app.core.network.utils.ApiResult

interface AlarmRepository {
    /**
     * @return: [AlarmPageResponse]
     */
    suspend fun getAllAlarms(page: Int): ApiResult

    /**
     * @return: [NetworkPostPreview]
     */
    suspend fun getPreviewPost(alarmId: Int): ApiResult

    suspend fun checkAlarm(alarmId: Int): ApiResult

}
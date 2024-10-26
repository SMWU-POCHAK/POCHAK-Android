package com.site.pochak.app.core.network.service

import com.site.pochak.app.core.network.model.AlarmPageResponse
import com.site.pochak.app.core.network.model.NetworkResponse
import com.site.pochak.app.core.network.model.NetworkPostPreview
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Alarm API
 *
 * @GET /api/v2/alarms: 알람 조회 API
 * @GET /api/v2/alarms/{alarmId}: 게시물 미리보기 API
 * @POST /api/v2/alarms/{alarmId}: 알람 확인 API
 *
 */
interface AlarmService {

    @GET("/api/v2/alarms")
    fun getAllAlarms(
        @Query("page") page: Int
    ): NetworkResponse<AlarmPageResponse>

    @GET("/api/v2/alarms/{alarmId}")
    fun getPreviewPost(
        @Path("alarmId") alarmId: Int
    ): NetworkResponse<NetworkPostPreview>

    @POST("/api/v2/alarms/{alarmId}")
    fun checkAlarm(
        @Path("alarmId") alarmId: Int
    ): NetworkResponse<Unit>

}
package com.site.pochak.app.feature.alarm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.alarm.AlarmRoute
import kotlinx.serialization.Serializable

@Serializable data object AlarmRoute

fun NavController.navigateToAlarm(navOptions: NavOptions? = null) = navigate(AlarmRoute, navOptions)

fun NavGraphBuilder.alarmScreen() {
    composable<AlarmRoute> {
        AlarmRoute()
    }
}
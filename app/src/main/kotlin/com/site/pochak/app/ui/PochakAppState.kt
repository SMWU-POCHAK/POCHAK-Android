package com.site.pochak.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.site.pochak.app.feature.alarm.navigation.navigateToAlarm
import com.site.pochak.app.feature.camera.navigation.navigateToCamera
import com.site.pochak.app.feature.camera.navigation.navigateToPost
import com.site.pochak.app.feature.home.navigation.navigateToHome
import com.site.pochak.app.feature.profile.navigation.navigateToProfile
import com.site.pochak.app.navigation.TopLevelDestination
import com.site.pochak.app.navigation.TopLevelDestination.ALARM
import com.site.pochak.app.navigation.TopLevelDestination.CAMERA
import com.site.pochak.app.navigation.TopLevelDestination.HOME
import com.site.pochak.app.navigation.TopLevelDestination.POST
import com.site.pochak.app.navigation.TopLevelDestination.PROFILE
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberPochakAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): PochakAppState {
    return remember(
        navController,
        coroutineScope,
    ) {
        PochakAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
class PochakAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) ?: false
            }
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(
        topLevelDestination: TopLevelDestination,
        inclusive: Boolean = false,
    ) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                this.inclusive = inclusive
                saveState = true
            }

            // 중복된 화면이 쌓이지 않도록 설정
            launchSingleTop = true

            restoreState = true
        }

        when (topLevelDestination) {
            HOME -> navController.navigateToHome(topLevelNavOptions)
            POST -> navController.navigateToPost(topLevelNavOptions)
            CAMERA -> navController.navigateToCamera(topLevelNavOptions)
            ALARM -> navController.navigateToAlarm(topLevelNavOptions)
            PROFILE -> navController.navigateToProfile(topLevelNavOptions)
        }
    }
}
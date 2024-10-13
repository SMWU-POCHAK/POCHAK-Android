package com.site.pochak.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.site.pochak.app.feature.alarm.navigation.alarmScreen
import com.site.pochak.app.feature.camera.navigation.cameraScreen
import com.site.pochak.app.feature.camera.navigation.postScreen
import com.site.pochak.app.feature.home.navigation.HomeRoute
import com.site.pochak.app.feature.home.navigation.homeScreen
import com.site.pochak.app.feature.login.navigation.LoginRoute
import com.site.pochak.app.feature.login.navigation.loginScreen
import com.site.pochak.app.feature.profile.navigation.profileScreen
import com.site.pochak.app.navigation.TopLevelDestination.HOME
import com.site.pochak.app.ui.PochakAppState

@Composable
fun PochakNavHost(
    appState: PochakAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = modifier,
    ) {
        loginScreen(onLoginSuccess = {
            // Login 성공 시 LoginRoute를 pop하고 HomeRoute로 이동한다.
            appState.navigateToTopLevelDestination(HOME, inclusive = true)
            navController.graph.setStartDestination(HomeRoute)
        })
        homeScreen()
        postScreen()
        cameraScreen()
        alarmScreen()
        profileScreen()
    }
}
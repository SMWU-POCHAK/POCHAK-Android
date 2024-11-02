package com.site.pochak.app.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.splash.SplashRoute
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable<SplashRoute> {
        SplashRoute(
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome,
        )
    }
}
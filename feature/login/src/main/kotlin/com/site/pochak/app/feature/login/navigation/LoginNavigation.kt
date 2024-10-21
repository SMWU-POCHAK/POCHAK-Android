package com.site.pochak.app.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.login.LoginRoute
import kotlinx.serialization.Serializable

@Serializable data object LoginRoute

fun NavController.navigateToLogin(navOptions: NavOptions? = null) = navigate(LoginRoute, navOptions)

fun NavGraphBuilder.loginScreen(onLoginSuccess: () -> Unit) {
    composable<LoginRoute> {
        LoginRoute(navigateToHome = onLoginSuccess)
    }
}
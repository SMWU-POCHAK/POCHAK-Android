package com.site.pochak.app.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        HomeRoute()
    }
}
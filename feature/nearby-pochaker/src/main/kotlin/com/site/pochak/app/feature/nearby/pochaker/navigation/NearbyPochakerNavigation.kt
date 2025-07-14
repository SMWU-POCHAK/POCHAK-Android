package com.site.pochak.app.feature.nearby.pochaker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.nearby.pochaker.NearbyPochakerRoute
import kotlinx.serialization.Serializable

@Serializable
data object NearbyPochakerRoute

fun NavController.navigateToNearbyPochaker(navOptions: NavOptions? = null) = navigate(NearbyPochakerRoute, navOptions)

fun NavGraphBuilder.nearbyPochakerScreen(
) {
    composable<NearbyPochakerRoute> {
        NearbyPochakerRoute(
        )
    }
}
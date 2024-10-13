package com.site.pochak.app.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable data object ProfileRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute> {
        ProfileRoute()
    }
}
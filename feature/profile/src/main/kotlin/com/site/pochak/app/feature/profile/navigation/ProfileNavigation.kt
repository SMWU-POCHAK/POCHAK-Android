package com.site.pochak.app.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable data class ProfileRoute(
    val handle: String?
)

fun NavController.navigateToProfile(
    navOptions: NavOptions? = null,
    handle: String? = null,
) = navigate(ProfileRoute(handle), navOptions)

fun NavGraphBuilder.profileScreen(
    navigateToPostDetail: (Int) -> Unit,
) {
    composable<ProfileRoute> {
        ProfileRoute(
            navigateToPostDetail = navigateToPostDetail,
        )
    }
}
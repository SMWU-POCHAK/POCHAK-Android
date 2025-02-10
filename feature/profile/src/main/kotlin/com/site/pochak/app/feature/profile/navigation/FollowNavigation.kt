package com.site.pochak.app.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.profile.FollowRoute
import kotlinx.serialization.Serializable

@Serializable data class FollowRoute(
    val handle: String,
    val followerCount: Int,
    val followingCount: Int,
    val selectedTab: Int,
)

fun NavController.navigateToFollow(
    navOptions: NavOptions? = null,
    handle: String,
    followerCount: Int,
    followingCount: Int,
    selectedTab: Int,
) = navigate(FollowRoute(handle, followerCount, followingCount, selectedTab), navOptions)

fun NavGraphBuilder.followScreen(
    onBack: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    composable<FollowRoute> {
        FollowRoute(
            onBack = onBack,
            navigateToProfile = navigateToProfile,
        )
    }
}
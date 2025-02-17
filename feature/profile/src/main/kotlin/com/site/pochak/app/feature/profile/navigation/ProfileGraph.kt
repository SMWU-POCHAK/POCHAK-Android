package com.site.pochak.app.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.site.pochak.app.feature.post.detail.navigation.postDetailScreen
import com.site.pochak.app.feature.profile.FollowRoute
import com.site.pochak.app.feature.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data class ProfileGraph(
    val handle: String?,
)

@Serializable
data object ProfileRoute

@Serializable
data class FollowRoute(
    val handle: String,
    val followerCount: Int,
    val followingCount: Int,
    val selectedTab: Int,
)

fun NavController.navigateToProfile(
    navOptions: NavOptions? = null,
    handle: String? = null,
) = navigate(ProfileGraph(handle), navOptions)

fun NavController.navigateToFollow(
    navOptions: NavOptions? = null,
    handle: String,
    followerCount: Int,
    followingCount: Int,
    selectedTab: Int,
) = navigate(FollowRoute(handle, followerCount, followingCount, selectedTab), navOptions)

internal fun NavGraphBuilder.profileScreen(
    navigateToPostDetail: (Int) -> Unit,
    navigateToProfileSetting: (String) -> Unit,
    navigateToFollow: (String, Int, Int, Int) -> Unit,
    navigateToSetting: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileRoute(
            navigateToPostDetail = navigateToPostDetail,
            navigateToProfileSetting = navigateToProfileSetting,
            navigateToFollow = navigateToFollow,
            navigateToSetting = navigateToSetting,
        )
    }
}

internal fun NavGraphBuilder.followScreen(
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

fun NavGraphBuilder.profileGraph(
    onBack: () -> Unit,
    navigateToPostDetail: (Int) -> Unit,
    navigateToProfileSetting: (String) -> Unit,
    navigateToFollow: (String, Int, Int, Int) -> Unit,
    navigateToProfile: (String) -> Unit,
    navigateToSetting: () -> Unit,
) {
    navigation<ProfileGraph>(startDestination = ProfileRoute) {
        profileScreen(
            navigateToPostDetail = navigateToPostDetail,
            navigateToProfileSetting = navigateToProfileSetting,
            navigateToFollow = navigateToFollow,
            navigateToSetting = navigateToSetting,
        )

        followScreen(
            onBack = onBack,
            navigateToProfile = navigateToProfile,
        )
    }
}
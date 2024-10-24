package com.site.pochak.app.feature.profile.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.profile.setting.ProfileSettingRoute
import kotlinx.serialization.Serializable

@Serializable data class ProfileSettingRoute(
    val loginInfoJson: String
)

fun NavController.navigateToProfileSetting(
    loginInfoJson: String,
    navOptions: NavOptions? = null
) = navigate(ProfileSettingRoute(loginInfoJson), navOptions)

fun NavGraphBuilder.profileSettingScreen(
    navigateToHome: () -> Unit,
    onBack: () -> Unit,
) {
    composable<ProfileSettingRoute> {
        ProfileSettingRoute(
            navigateToHome = navigateToHome,
            onBack = onBack,
        )
    }
}
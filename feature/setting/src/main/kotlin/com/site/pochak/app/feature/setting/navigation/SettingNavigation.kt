package com.site.pochak.app.feature.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.site.pochak.app.feature.setting.BlockUserRoute
import com.site.pochak.app.feature.setting.SettingRoute
import kotlinx.serialization.Serializable

@Serializable data object SettingGraph

@Serializable data object SettingRoute
@Serializable data object BlockUserRoute

fun NavController.navigateToSetting(navOptions: NavOptions? = null) = navigate(SettingRoute, navOptions)

fun NavController.navigateToBlockUser(navOptions: NavOptions? = null) = navigate(BlockUserRoute, navOptions)

fun NavGraphBuilder.settingGraph(
    onBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToBlockUser: () -> Unit,
) {
    navigation<SettingGraph>(
        startDestination = SettingRoute,
    ) {
        composable<SettingRoute> {
            SettingRoute(
                onBack = onBack,
                navigateToLogin = navigateToLogin,
                navigateToBlockUser = { navigateToBlockUser() },
            )
        }

        composable<BlockUserRoute> {
            BlockUserRoute(
                onBack = onBack,
            )
        }
    }
}
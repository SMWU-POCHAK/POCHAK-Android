package com.site.pochak.app.feature.camera.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.camera.CameraRoute
import kotlinx.serialization.Serializable

@Serializable data object CameraRoute

fun NavController.navigateToCamera(navOptions: NavOptions? = null) = navigate(CameraRoute, navOptions)

fun NavGraphBuilder.cameraScreen() {
    composable<CameraRoute> {
        CameraRoute()
    }
}
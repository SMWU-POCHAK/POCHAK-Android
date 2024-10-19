package com.site.pochak.app.feature.camera.navigation

import android.graphics.Bitmap
import android.graphics.Camera
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.site.pochak.app.feature.camera.CameraRoute
import com.site.pochak.app.feature.camera.CameraScreen
import com.site.pochak.app.feature.camera.CameraViewModel
import com.site.pochak.app.feature.camera.UploadRoute
import kotlinx.serialization.Serializable

@Serializable data object CameraRoute
@Serializable data object UploadRoute

fun NavController.navigateToCamera(navOptions: NavOptions? = null) = navigate(CameraRoute, navOptions)

fun NavGraphBuilder.cameraScreen(
    navigateToUpload: () -> Unit,
) {
    composable<CameraRoute> {
        CameraRoute(
            navigateToUpload = navigateToUpload,
        )
    }
}

fun NavController.navigateToUpload(
    navOptions: NavOptions? = null
) = navigate(UploadRoute, navOptions)

fun NavGraphBuilder.uploadScreen(
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable<UploadRoute> {
        UploadRoute(
            navigateToHome = navigateToHome,
            onBackClick = onBackClick,
        )
    }
}
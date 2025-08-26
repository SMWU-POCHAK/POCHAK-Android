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

@Serializable data class CameraRoute(
    val nearbyPochakerhandle: String? = null
)

@Serializable data class UploadRoute(
    val nearbyPochakerhandle: String? = null
)

fun NavController.navigateToCamera(
    nearbyPochakerhandle: String? = null,
    navOptions: NavOptions? = null
) = navigate(CameraRoute(nearbyPochakerhandle), navOptions)

fun NavGraphBuilder.cameraScreen(
    navigateToUpload: (String?) -> Unit,
) {
    composable<CameraRoute> {
        CameraRoute(
            navigateToUpload = navigateToUpload
        )
    }
}

fun NavController.navigateToUpload(
    nearbyPochakerhandle: String? = null,
    navOptions: NavOptions? = null
) = navigate(UploadRoute(nearbyPochakerhandle), navOptions)

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
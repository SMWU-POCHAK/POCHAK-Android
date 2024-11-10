package com.site.pochak.app.feature.post.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.post.PostRoute
import kotlinx.serialization.Serializable

@Serializable data object PostRoute

fun NavController.navigateToPost(navOptions: NavOptions? = null) = navigate(PostRoute, navOptions)

fun NavGraphBuilder.postScreen() {
    composable<PostRoute> {
        PostRoute()
    }
}
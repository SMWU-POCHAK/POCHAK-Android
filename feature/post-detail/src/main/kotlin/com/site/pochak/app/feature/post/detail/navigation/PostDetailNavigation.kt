package com.site.pochak.app.feature.post.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.post.detail.PostDetailRoute
import kotlinx.serialization.Serializable

@Serializable data class PostDetailRoute(
    val postId: Int
)

fun NavController.navigateToPostDetail(
    postId: Int,
    navOptions: NavOptions? = null
) = navigate(PostDetailRoute(postId), navOptions)

fun NavGraphBuilder.postDetailScreen(
    onBack: () -> Unit,
) {
    composable<PostDetailRoute> {
        PostDetailRoute(
            onBack = onBack,
        )
    }
}
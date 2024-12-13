package com.site.pochak.app.feature.post.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.site.pochak.app.feature.post.PostRoute
import com.site.pochak.app.feature.post.SearchHistoryRoute
import kotlinx.serialization.Serializable

@Serializable data object PostRoute
@Serializable data object SearchHistoryRoute

fun NavController.navigateToPost(navOptions: NavOptions? = null) = navigate(PostRoute, navOptions)
fun NavController.navigateToSearchHistory(navOptions: NavOptions? = null) = navigate(SearchHistoryRoute, navOptions)

fun NavGraphBuilder.postScreen(
    navigateToSearchHistory: () -> Unit,
) {
    composable<PostRoute> {
        PostRoute(
            navigateToSearchHistory = navigateToSearchHistory
        )
    }
}

fun NavGraphBuilder.searchHistoryScreen(
) {
    composable<SearchHistoryRoute> {
        SearchHistoryRoute()
    }
}
package com.site.pochak.app.feature.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray0_5
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.ui.postFeed

@Composable
internal fun PostRoute(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel(),
    navigateToSearchHistory: () -> Unit,
) {
    val postPosts = viewModel.postPosts.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    PostScreen(
        modifier = modifier,
        postPosts = postPosts.value,
        isLoading = isLoading.value,
        isRefreshing = isRefreshing.value,
        onLoadPage = viewModel::loadPage,
        navigateToSearchHistory = navigateToSearchHistory,
    )
}

@Composable
internal fun PostScreen(
    modifier: Modifier = Modifier,
    postPosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
    navigateToSearchHistory: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
    ) {
        postSearchBar(
            modifier = Modifier.padding(horizontal = HorizontalPadding),
            navigateToSearchHistory = navigateToSearchHistory,
        )

        PostContent(
            modifier = Modifier.fillMaxSize(),
            postPosts = postPosts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
        )
    }
}

@Composable
private fun postSearchBar(
    modifier: Modifier = Modifier,
    navigateToSearchHistory: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 24.dp)
                .height(48.dp)
                .align(Alignment.TopCenter)  // Aligns the Row to the top center of the Box
                .background(Gray0_5, shape = RoundedCornerShape(18.dp))
                .clickable { navigateToSearchHistory() }
        ) {
            Icon(
                painter = painterResource(id = PochakIcons.Search),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(24.dp)
            )

            Text(
                text = stringResource(id = R.string.feature_post_search_placeholder),
                color = Gray03,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}


@Composable
private fun PostContent(
    modifier: Modifier = Modifier,
    postPosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    // Load the first page when the screen is launched
    LaunchedEffect(Unit) {
        if (postPosts.isEmpty() && !isLoading) {
            onLoadPage(true)
        }
    }

    Text(
        text = stringResource(id = R.string.feature_post_realtime_popular),
        style = MaterialTheme.typography.titleMedium,
        color = Color.Black,
        modifier = Modifier
            .padding(horizontal = HorizontalPadding)
    )

    Box(modifier = modifier.fillMaxSize()) {
        HomePostContent(
            modifier = Modifier.fillMaxSize(),
            homePosts = postPosts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomePostContent(
    modifier: Modifier = Modifier,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        refreshEnabled = true,
        isRefreshing = isRefreshing,
        columns = GridCells.Fixed(3),
        loadMore = onLoadPage,
    ) {
        postFeed(homePosts)

        if (isLoading && !isRefreshing) {
            item { }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
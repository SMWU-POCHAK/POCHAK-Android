package com.site.pochak.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.theme.Gray02
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.ui.postFeed

private const val TAG = "HomeScreen"

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homePosts = viewModel.homePosts.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        homePosts = homePosts.value,
        isLoading = isLoading.value,
        isRefreshing = isRefreshing.value,
        onLoadPage = viewModel::loadPage,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            centerContent = {
                Image(
                    painter = painterResource(id = R.drawable.feature_home_logo_small),
                    contentDescription = "Pochak Logo",
                )
            }
        )

        HomeContent(
            modifier = Modifier.fillMaxSize(),
            homePosts = homePosts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    // Load the first page when the screen is launched
    LaunchedEffect(Unit) {
        if (homePosts.isEmpty() && !isLoading) {
            onLoadPage(true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (homePosts.isEmpty() && !isLoading) {
            HomePostNoPhoto(Modifier.align(Alignment.Center))
        } else {
            HomePostContent(
                modifier = Modifier.fillMaxSize(),
                homePosts = homePosts,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                onLoadPage = onLoadPage,
            )
        }
    }
}

@Composable
private fun HomePostNoPhoto(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.feature_home_no_photo),
            contentDescription = "No Posts",
        )

        Text(
            text = stringResource(id = R.string.feature_home_no_photo),
            style = MaterialTheme.typography.titleMedium.copy(color = Gray02),
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
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
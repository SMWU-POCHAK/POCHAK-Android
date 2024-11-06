package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshableLazyVerticalGrid(
    modifier: Modifier = Modifier,
    refreshEnabled: Boolean = false,
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    isRefreshing: Boolean = false,
    threshold: Dp = 80.dp,
    state: LazyGridState = rememberLazyGridState(),
    columns: GridCells = GridCells.Fixed(1),
    contentPadding: PaddingValues = PaddingValues(horizontal = HorizontalPadding, vertical = VerticalPadding),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    loadMoreLimitCount: Int = 3,
    loadMore: (isRefresh: Boolean) -> Unit = {},
    content: LazyGridScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = refreshState,
                enabled = refreshEnabled,
                threshold = threshold,
                onRefresh = { loadMore(true) }
            )
    ) {
        RefreshIndicator(refreshState, threshold)

        AutoLoadLazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = refreshState.distanceFraction.dp * threshold.value),
            state = state,
            columns = columns,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            loadMoreLimitCount = loadMoreLimitCount,
            loadMore = { loadMore(false) },
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RefreshIndicator(
    refreshState: PullToRefreshState,
    threshold: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                clipRect(
                    top = 0f,
                    left = -Float.MAX_VALUE,
                    right = Float.MAX_VALUE,
                    bottom = Float.MAX_VALUE
                ) {
                    this@drawWithContent.drawContent()
                }
            }
            .graphicsLayer {
                translationY =
                    refreshState.distanceFraction * threshold.roundToPx() * 0.75f - size.height
            },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AutoLoadLazyVerticalGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    columns: GridCells = GridCells.Fixed(3),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    loadMoreLimitCount: Int = 9,
    loadMore: () -> Unit = {},
    content: LazyGridScope.() -> Unit,
) {
    state.onLoadMore(limitCount = loadMoreLimitCount, action = loadMore)

    LazyVerticalGrid(
        modifier = modifier,
        state = state,
        columns = columns,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun LazyGridState.onLoadMore(
    limitCount: Int,
    loadOnBottom: Boolean = true,
    action: () -> Unit
) {
    val reached by remember {
        derivedStateOf {
            reachedBottom(limitCount, loadOnBottom)
        }
    }

    LaunchedEffect(reached) {
        if (reached) action()
    }
}

private fun LazyGridState.reachedBottom(
    limitCount: Int,
    triggerOnEnd: Boolean = false,
): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()

    return (triggerOnEnd && lastVisibleItem?.index == layoutInfo.totalItemsCount - 1)
            || lastVisibleItem?.index != 0 && lastVisibleItem?.index == layoutInfo.totalItemsCount - (limitCount + 1)
}

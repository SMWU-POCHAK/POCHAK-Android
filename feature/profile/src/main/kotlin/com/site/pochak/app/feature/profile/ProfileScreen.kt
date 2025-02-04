package com.site.pochak.app.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.FollowButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.PostImage
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.component.noRippleClickable
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Yellow00
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.network.model.NetworkProfile

@Composable
internal fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateToPostDetail: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pochakedPosts = viewModel.pochakedPosts.collectAsLazyPagingItems()
    val pochakPosts = viewModel.pochakPosts.collectAsLazyPagingItems()

    ProfileScreen(
        modifier = modifier,
        uiState = uiState,
        isFollow = viewModel.isFollow,
        pochakedPosts = pochakedPosts,
        pochakPosts = pochakPosts,
        navigateToPostDetail = navigateToPostDetail,
        onClickFollow = viewModel::followMember,
    )
}

@Composable
internal fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    isFollow: Boolean?,
    pochakedPosts: LazyPagingItems<Post>,
    pochakPosts: LazyPagingItems<Post>,
    navigateToPostDetail: (Int) -> Unit,
    onClickFollow: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ProfileUiState.Success -> {
                val profile = uiState.profile

                ProfileContent(
                    profile = profile,
                    isFollow = isFollow,
                    pochakedPosts = pochakedPosts,
                    pochakPosts = pochakPosts,
                    navigateToPostDetail = navigateToPostDetail,
                    onClickFollow = onClickFollow,
                )
            }

            is ProfileUiState.Error -> {
                Text(text = uiState.message)
            }
        }
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    profile: NetworkProfile,
    isFollow: Boolean?,
    pochakedPosts: LazyPagingItems<Post>,
    pochakPosts: LazyPagingItems<Post>,
    navigateToPostDetail: (Int) -> Unit,
    onClickFollow: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            leftContent = {
                Text(
                    text = "@${profile.handle}",
                    style = PochakTextStyle.body0,
                    modifier = Modifier.padding(start = HorizontalPadding),
                )
            },
            rightContent = {
                IconButton(onClick = { /* TODO */ }) {
                    Image(
                        painter = painterResource(id = PochakIcons.More),
                        contentDescription = null
                    )
                }
            }
        )

        // Profile image and info
        ProfileTopContent(
            profile = profile
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Follow button
        isFollow?.let {
            FollowButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPadding),
                isFollow = it,
                paddingValues = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                onClick = onClickFollow,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pochaked and Pochak posts
        ProfileTabContent(
            pochakedPosts = pochakedPosts,
            pochakPosts = pochakPosts,
            navigateToPostDetail = navigateToPostDetail,
        )
    }
}

@Composable
private fun ProfileTopContent(
    modifier: Modifier = Modifier,
    profile: NetworkProfile,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile image and info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HorizontalPadding),
        ) {
            Box(modifier = Modifier.size(116.dp)) {
                CircleCropAsyncImage(
                    imageUrl = profile.profileImage,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            color = Yellow00,
                            shape = CircleShape
                        ),
                )

                Image(
                    painter = painterResource(id = PochakIcons.EditProfile),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(y = 12.dp)
                        .clip(CircleShape)
                        .noRippleClickable {  }
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                )
            }

            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = profile.name,
                    style = PochakTextStyle.body1,
                )

                Text(
                    text = profile.message,
                    style = PochakTextStyle.body3,
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // following and followers count
        Row(horizontalArrangement = Arrangement.spacedBy(64.dp)) {
            ProfileTextAndCount(
                text = "게시글",
                count = profile.totalPostNum,
            )

            ProfileTextAndCount(
                text = "팔로워",
                count = profile.followerCount,
            )

            ProfileTextAndCount(
                text = "팔로잉",
                count = profile.followingCount,
            )
        }
    }
}

@Composable
private fun ProfileTextAndCount(
    modifier: Modifier = Modifier,
    text: String,
    count: Int?,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            style = PochakTextStyle.body3_1,
        )

        Text(
            text = "${count ?: 0}",
            style = PochakTextStyle.body3,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTabContent(
    modifier: Modifier = Modifier,
    pochakedPosts: LazyPagingItems<Post>,
    pochakPosts: LazyPagingItems<Post>,
    navigateToPostDetail: (Int) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val pochakedIsRefreshing = pochakedPosts.loadState.refresh is LoadState.Loading
    val pochakIsRefreshing = pochakPosts.loadState.refresh is LoadState.Loading

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "POCHAKED",
                style = PochakTextStyle.body1,
                modifier = Modifier
                    .weight(1f)
                    .drawBottomBorder(selectedTabIndex == 0)
                    .clickable { selectedTabIndex = 0 }
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
                color = if (selectedTabIndex == 0) Color.Black else Color(0xFFCECCC8),
            )

            Text(
                text = "POCHAK",
                style = PochakTextStyle.body1,
                modifier = Modifier
                    .weight(1f)
                    .drawBottomBorder(selectedTabIndex == 1)
                    .clickable { selectedTabIndex = 1 }
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
                color = if (selectedTabIndex == 1) Color.Black else Color(0xFFCECCC8),
            )
        }

        RefreshableLazyVerticalGrid(
            refreshEnabled = false,
            isRefreshing = if (selectedTabIndex == 0) pochakedIsRefreshing else pochakIsRefreshing,
            onRefresh = {
                if (selectedTabIndex == 0) {
                    pochakedPosts.refresh()
                } else {
                    pochakPosts.refresh()
                }
            },
        ) {
            if (selectedTabIndex == 0) {
                items(pochakedPosts.itemCount) { index ->
                    val post = pochakedPosts[index]

                    post?.let {
                        PostImage(
                            imageUrl = it.postImage,
                            id = it.postId,
                            onClick = { navigateToPostDetail(post.postId) }
                        )
                    }
                }
            } else {
                items(pochakPosts.itemCount) { index ->
                    val post = pochakPosts[index]

                    post?.let {
                        PostImage(
                            imageUrl = it.postImage,
                            id = it.postId,
                            onClick = { navigateToPostDetail(post.postId) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun Modifier.drawBottomBorder(
    enabled: Boolean = true,
    color: Color = Yellow00,
    strokeWidth: Dp = 4.dp,
): Modifier = drawBehind {
    if (enabled) {
        val strokeWidth = strokeWidth.toPx()
        val y = size.height - strokeWidth / 2

        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
}
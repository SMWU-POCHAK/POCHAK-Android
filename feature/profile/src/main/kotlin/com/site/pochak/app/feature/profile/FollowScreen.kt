package com.site.pochak.app.feature.profile

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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.FollowButton
import com.site.pochak.app.core.designsystem.component.MoreButton
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.PostImage
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.component.noRippleClickable
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.PochakTheme
import com.site.pochak.app.core.designsystem.theme.Yellow00
import com.site.pochak.app.core.model.data.Member
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun FollowRoute(
    modifier: Modifier = Modifier,
    viewModel: FollowViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val followerList = viewModel.followerList.collectAsLazyPagingItems()
    val followingList = viewModel.followingList.collectAsLazyPagingItems()

    FollowScreen(
        modifier = modifier,
        onBack = onBack,
        uiState = uiState,
        followerList = followerList,
        followingList = followingList,
        navigateToProfile = navigateToProfile,
    )
}

@Composable
internal fun FollowScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: FollowUiState,
    followerList: LazyPagingItems<Member>,
    followingList: LazyPagingItems<Member>,
    navigateToProfile: (String) -> Unit,
) {
   Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
       when (uiState) {
           is FollowUiState.Loading -> {
               CircularProgressIndicator()
           }

           is FollowUiState.Success -> {
               FollowContent(
                   uiState = uiState,
                   onBack = onBack,
                   followerList = followerList,
                   followingList = followingList,
                   navigateToProfile = navigateToProfile,
               )
           }

           else -> Unit
       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: FollowUiState.Success,
    followerList: LazyPagingItems<Member>,
    followingList: LazyPagingItems<Member>,
    navigateToProfile: (String) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(uiState.selectedTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            leftContent = { BackButton(onClick = onBack) },
            centerContent = {
                Text(
                    text = uiState.handle,
                    style = PochakTextStyle.body0,
                )
            },
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${uiState.followerCount} 팔로워",
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
                text = "${uiState.followingCount} 팔로잉",
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
            columns = GridCells.Fixed(1),
        ) {
            if (selectedTabIndex == 0) {
                items(followerList.itemCount) { index ->
                    val member = followerList[index]

                    member?.let {
                        MemberItem(
                            member = it,
                            navigateToProfile = navigateToProfile,
                            onFollow = {}
                        )
                    }
                }
            } else {
                items(followingList.itemCount) { index ->
                    val member = followingList[index]

                    member?.let {
                        MemberItem(
                            member = it,
                            navigateToProfile = navigateToProfile,
                            onFollow = {}
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

@Composable
private fun MemberItem(
    member: Member,
    navigateToProfile: (String) -> Unit,
    onFollow: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .noRippleClickable { navigateToProfile(member.handle) }
    ) {
        CircleCropAsyncImage(
            modifier = Modifier.size(52.dp),
            imageUrl = member.profileImage,
            onClick = { navigateToProfile(member.handle) },
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = member.handle,
                style = PochakTextStyle.body3_1,
            )

            Text(
                text = member.name,
                style = PochakTextStyle.body3,
            )
        }

        member.isFollow?.let {
            FollowButton(
                isFollow = it,
            )
        }
    }

    HorizontalDivider(color = Gray01)
}
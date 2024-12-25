package com.site.pochak.app.feature.post.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.FollowButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.MoreButton
import com.site.pochak.app.core.designsystem.component.PochakAlertDialog
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.noRippleClickable
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.*

@Composable
fun PostDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val postDetailUiState by viewModel.postDetailUiState.collectAsStateWithLifecycle()

    PostDetailScreen(
        modifier = modifier,
        onBack = onBack,
        uiState = postDetailUiState,
    )
}

@Composable
internal fun PostDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: PostDetailUiState,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            PostDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is PostDetailUiState.Success -> {
                PostDetailContent(
                    onBack = onBack,
                    postDetail = uiState.postDetail,
                )
            }

            PostDetailUiState.Error -> {
                PochakAlertDialog(
                    onDismiss = onBack,
                    titleText = stringResource(id = R.string.feature_post_detail_error_title),
                    messageText = stringResource(id = R.string.feature_post_detail_error_message),
                    confirmButtonText = stringResource(id = R.string.feature_post_detail_error_confirm),
                    onConfirmClick = onBack,
                )
            }
        }
    }
}

@Composable
private fun PostDetailContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    postDetail: NetworkPostDetail,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            leftContent = { BackButton(onClick = onBack) },
            centerContent = {
                Text(
                    text = postDetail.ownerHandle + stringResource(id = R.string.feature_post_detail_title_suffix),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            rightContent = {
                MoreButton { }
            }
        )

        ProfileAndFollowButton(
            postDetail = postDetail,
        )

        // 게시물 이미지
        AsyncImage(
            model = postDetail.postImage,
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HorizontalPadding, vertical = 8.dp),
        )

        // 캡션, 좋아요, 댓글
        CaptionAndIcons(
            postDetail = postDetail,
            onClickLike = {},
            onClickComment = { }
        )

        // 최근 댓글
        postDetail.recentComment?.let {
            RecentComment(
                recentComment = it,
                onClickComment = { }
            )
        }
    }
}

@Composable
private fun ProfileAndFollowButton(
    modifier: Modifier = Modifier,
    postDetail: NetworkPostDetail,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCropAsyncImage(
                modifier = Modifier.size(50.dp),
                imageUrl = postDetail.ownerProfileImage,
                contentDescription = "profile image",
                onClick = { /* owner 프로필로 이동 */ },
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 태그 최대 2개 표시, 3개 이상일 경우 "태그1 · 태그2 · ..." 형태로 표시
                val maxTagCount = 2
                Text(
                    text = postDetail.tagList.map { it.handle }.take(maxTagCount)
                        .joinToString(" · ") { it + "님" } + if (postDetail.tagList.size > maxTagCount) "..." else "",
                    style = PochakTextStyle.body1,
                    modifier = Modifier.noRippleClickable { /* 태그 모달 열기 */ }
                )

                Text(
                    text = postDetail.ownerHandle + "님이 포착",
                    style = PochakTextStyle.body4,
                    modifier = Modifier.noRippleClickable { /* owner 프로필로 이동 */ }
                )
            }
        }

        postDetail.isFollow?.let {
            FollowButton(
                isFollow = it,
                onClick = { /* 팔로우/팔로잉 버튼 클릭 시 동작 */ }
            )
        }
    }
}

@Composable
private fun CaptionAndIcons(
    postDetail: NetworkPostDetail,
    onClickLike: () -> Unit,
    onClickComment: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = HorizontalPadding, end = 10.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = postDetail.ownerHandle,
                style = PochakTextStyle.body3_1,
            )

            Text(
                text = postDetail.caption,
                style = PochakTextStyle.body3,
            )
        }

        Row {
            IconButton(onClick = onClickLike) {
                Image(
                    painter = painterResource(
                        if (postDetail.isLike) PochakIcons.HeartFilled
                        else PochakIcons.Heart
                    ),
                    contentDescription = "Like"
                )
            }
            IconButton(onClick = onClickComment) {
                Image(
                    painter = painterResource(
                        if (postDetail.recentComment != null) PochakIcons.CommentFilled
                        else PochakIcons.Comment
                    ),
                    contentDescription = "Comment"
                )
            }
        }
    }
}

@Composable
private fun RecentComment(
    recentComment: NetworkComment,
    onClickComment: () -> Unit,
) {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = HorizontalPadding),
        thickness = 1.dp,
        color = Gray01
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = recentComment.handle,
                style = PochakTextStyle.body3_1,
            )

            Text(
                text = recentComment.content,
                style = PochakTextStyle.body3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = "더보기",
            style = PochakTextStyle.body3,
            color = Gray05,
            modifier = Modifier.clickable(onClick = onClickComment)
        )
    }
}

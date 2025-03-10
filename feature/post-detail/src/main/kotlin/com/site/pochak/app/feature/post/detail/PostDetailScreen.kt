package com.site.pochak.app.feature.post.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.FollowButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.MoreButton
import com.site.pochak.app.core.designsystem.component.PochakAlertDialog
import com.site.pochak.app.core.designsystem.component.PochakNavigationTopAppBar
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.VerticalPadding
import com.site.pochak.app.core.designsystem.component.noRippleClickable
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.designsystem.theme.PochakTheme
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.network.model.NetworkComment
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.core.network.model.NetworkTag
import com.site.pochak.app.core.ui.ElapsedTimeText
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.*
import com.site.pochak.app.feature.post.detail.comment.CommentViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun PostDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    PostDetailScreen(
        modifier = modifier,
        onBack = onBack,
        uiState = uiState,
        actionState = actionState,
        isFollow = viewModel.isFollow,
        onClickFollow = viewModel::followMember,
        isLike = viewModel.isLike,
        onClickLike = viewModel::likePost,
        deletePost = viewModel::deletePost,
        reportPost = viewModel::reportPost,
        navigateToProfile = navigateToProfile,
    )
}

@Composable
internal fun PostDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: PostDetailUiState,
    actionState: PostDetailActionState,
    isFollow: Boolean?,
    onClickFollow: () -> Unit,
    isLike: Boolean,
    onClickLike: () -> Unit,
    deletePost: () -> Unit,
    reportPost: (String) -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is PostDetailUiState.Success -> {
                PostContent(
                    onBack = onBack,
                    postDetail = uiState.postDetail,
                    actionState = actionState,
                    isFollow = isFollow,
                    onClickFollow = onClickFollow,
                    isLike = isLike,
                    onClickLike = onClickLike,
                    deletePost = deletePost,
                    reportPost = reportPost,
                    navigateToProfile = navigateToProfile,
                )
            }

            PostDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

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
internal fun PostContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    postDetail: NetworkPostDetail,
    actionState: PostDetailActionState,
    isFollow: Boolean?,
    onClickFollow: () -> Unit,
    isLike: Boolean,
    onClickLike: () -> Unit,
    deletePost: () -> Unit,
    reportPost: (String) -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    var bottomSheetState by remember { mutableStateOf(CLOSED) }

    fun changeBottomSheetState(newState: PostDetailBottomSheetState) {
        bottomSheetState = newState
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            PochakNavigationTopAppBar(
                onBack = onBack,
                title = postDetail.ownerHandle + stringResource(id = R.string.feature_post_detail_title_suffix),
                rightContent = { MoreButton { changeBottomSheetState(MORE) } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            PostHeader(
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                postDetail = postDetail,
                onClickTag = { changeBottomSheetState(TAG) },
                onClickFollow = onClickFollow,
                navigateToProfile = navigateToProfile,
            )

            // 게시물 이미지
            AsyncImage(
                model = postDetail.postImage,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPadding, vertical = 8.dp)
                     // TODO: Remove preview
                    .aspectRatio(3 / 4f)
                    .background(Color.Gray),
            )

            PostFooter(
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                postDetail = postDetail,
                onClickComment = { changeBottomSheetState(COMMENT) },
                isLike = isLike,
                onClickLike = onClickLike,
                navigateToProfile = navigateToProfile,
            )
        }

        if (bottomSheetState != CLOSED) {
            PostDetailBottomSheetContent(
                state = bottomSheetState,
                postDetail = postDetail,
                onDismiss = { changeBottomSheetState(CLOSED) },
                changeState = ::changeBottomSheetState,
                onDelete = deletePost,
                reportPost = reportPost,
                navigateToProfile = navigateToProfile,
            )
        }
    }
}

// OwnerProfile, TagList, OwnerHandle, FollowButton
@Composable
private fun PostHeader(
    modifier: Modifier = Modifier,
    postDetail: NetworkPostDetail,
    onClickTag: () -> Unit,
    onClickFollow: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    val MAX_TAG_COUNT = 2
    val tagsText = postDetail.tagList.map { it.handle }
        .take(MAX_TAG_COUNT)
        .joinToString(" · ") { "$it 님" } +
            if (postDetail.tagList.size > MAX_TAG_COUNT) "..." else ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // OwnerProfileImage
            CircleCropAsyncImage(
                modifier = Modifier.size(50.dp),
                contentDescription = "Owner Profile Image",
                imageUrl = postDetail.ownerProfileImage,
                onClick = { navigateToProfile(postDetail.ownerHandle) }
            )

            // TagList, OwnerHandle, CreatedDate
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // 태그 최대 2개 표시, 3개 이상일 경우 "태그1 · 태그2 · ..." 형태로 표시
                Text(
                    text = tagsText,
                    style = PochakTextStyle.body1,
                    overflow = TextOverflow.Ellipsis, // 태그 표시가 길 경우 텍스트 생략
                    modifier = Modifier.noRippleClickable(onClick = onClickTag)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${postDetail.ownerHandle}님이 포착",
                        style = PochakTextStyle.body4,
                        modifier = Modifier.noRippleClickable { navigateToProfile(postDetail.ownerHandle) }
                    )

                    ElapsedTimeText(
                        createdDate = postDetail.allowedDate,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }

        // FollowButton
        postDetail.isFollow?.let { isFollow ->
            FollowButton(
                isFollow = isFollow,
                onClick = { onClickFollow() }
            )
        }
    }
}

@Composable
private fun PostFooter(
    modifier: Modifier = Modifier,
    postDetail: NetworkPostDetail,
    isLike: Boolean = false,
    onClickLike: () -> Unit,
    onClickComment: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = VerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.clickable { navigateToProfile(postDetail.ownerHandle) },
                text = postDetail.ownerHandle,
                style = PochakTextStyle.body3_1,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = postDetail.caption,
                style = PochakTextStyle.body3,
            )

            Row {
                IconButton(onClick = onClickLike) {
                    Image(
                        painter = painterResource(
                            if (isLike) PochakIcons.HeartFilled
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

        // Comment
        postDetail.recentComment?.let { recentComment ->
            HorizontalDivider(color = Gray01)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = VerticalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.clickable { navigateToProfile(recentComment.handle) },
                    text = recentComment.handle,
                    style = PochakTextStyle.body3_1,
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = recentComment.content,
                    style = PochakTextStyle.body3,
                )

                Text(
                    modifier = Modifier.noRippleClickable(onClick = onClickComment),
                    text = stringResource(id = R.string.feature_post_detail_comment_more_text),
                    style = PochakTextStyle.body3,
                    color = Gray05,
                )
            }
        }
    }
}

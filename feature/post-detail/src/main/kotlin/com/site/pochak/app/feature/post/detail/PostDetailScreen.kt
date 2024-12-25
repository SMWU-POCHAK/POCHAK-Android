package com.site.pochak.app.feature.post.detail

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
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
        isFollow = viewModel.isFollow,
        onClickFollow = viewModel::followMember,
        isLike = viewModel.isLike,
        onClickLike = viewModel::likePost,
        postDetailDeleteState = viewModel.postDetailDeleteState,
        deletePost = viewModel::deletePost,
        reportPost = viewModel::reportPost,
    )
}

@Composable
internal fun PostDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: PostDetailUiState,
    isFollow: Boolean?,
    onClickFollow: () -> Unit,
    isLike: Boolean,
    onClickLike: () -> Unit,
    postDetailDeleteState: PostDetailDeleteState,
    deletePost: () -> Unit,
    reportPost: (String) -> Unit,
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
                    isFollow = isFollow,
                    onClickFollow = onClickFollow,
                    isLike = isLike,
                    onClickLike = onClickLike,
                    postDetailDeleteState = postDetailDeleteState,
                    deletePost = deletePost,
                    reportPost = reportPost,
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
    isFollow: Boolean?,
    onClickFollow: () -> Unit,
    isLike: Boolean,
    onClickLike: () -> Unit,
    postDetailDeleteState: PostDetailDeleteState,
    deletePost: () -> Unit,
    reportPost: (String) -> Unit,
) {
    var state by remember { mutableStateOf(CLOSED) }

    // 게시물 삭제 성공 시 뒤로가기
    LaunchedEffect(postDetailDeleteState) {
        if (postDetailDeleteState == PostDetailDeleteState.Success) {
            onBack()
        }
    }

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
                MoreButton { state = MORE }
            }
        )

        ProfileAndFollowButton(
            postDetail = postDetail,
            isFollow = isFollow,
            onClickFollow = onClickFollow,
            onClickTag = { state = TAG },
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
            isLike = isLike,
            onClickLike = onClickLike,
            onClickComment = { state = COMMENT },
        )

        // 최근 댓글
        postDetail.recentComment?.let {
            RecentComment(
                recentComment = it,
                onClickComment = { state = COMMENT },
            )
        }
    }

    PostDetailBottomSheetContent(
        state = state,
        postDetail = postDetail,
        onDismiss = { state = CLOSED },
        changeState = { state = it },
        onDelete = deletePost,
        reportPost = reportPost,
    )
}

@Composable
private fun ProfileAndFollowButton(
    modifier: Modifier = Modifier,
    postDetail: NetworkPostDetail,
    isFollow: Boolean?,
    onClickFollow: () -> Unit,
    onClickTag: () -> Unit,
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
                    overflow = TextOverflow.Ellipsis, // 태그 표시가 길 경우 텍스트 생략
                    modifier = Modifier.noRippleClickable(onClick = onClickTag)
                )

                Text(
                    text = postDetail.ownerHandle + "님이 포착",
                    style = PochakTextStyle.body4,
                    modifier = Modifier.noRippleClickable { /* owner 프로필로 이동 */ }
                )
            }
        }

        isFollow?.let {
            FollowButton(
                isFollow = it,
                onClick = onClickFollow,
            )
        }
    }
}

@Composable
private fun CaptionAndIcons(
    postDetail: NetworkPostDetail,
    isLike: Boolean,
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
            modifier = Modifier.noRippleClickable(onClick = onClickComment)
        )
    }
}
//@Preview
//@Composable
//private fun PostDetailScreenPreview() {
//    PochakTheme {
//        PostDetailScreen(
//            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
//            onBack = { },
//            uiState = PostDetailUiState.Success(
//                NetworkPostDetail(
//                    ownerHandle = "ownerHandle",
//                    ownerProfileImage = "https://www.example.com/image.jpg",
//                    postImage = "https://www.example.com/image.jpg",
//                    caption = "caption1 caption1",
//                    tagList = listOf(
//                        NetworkTag(
//                            memberId = 1,
//                            handle = "tagHandle",
//                            name = "tagName",
//                            profileImage = "https://www.example.com/image.jpg"
//                        )
//                    ),
//                    isLike = true,
//                    recentComment = NetworkComment(
//                        commentId = 1,
//                        memberId = 1,
//                        profileImage = "https://www.example.com/image.jpg",
//                        handle = "commentHandle",
//                        createdDate = "2021-01-01",
//                        content = "content content",
//                    ),
//                    ownerId = 1,
//                    isFollow = false,
//                    likeCount = 0,
//                )
//            ),
//        )
//    }
//}

//@Composable
//private fun PostDetailContent(
//    modifier: Modifier = Modifier,
//    onBack: () -> Unit,
//    postDetail: NetworkPostDetail,
//    onClickLike: () -> Unit,
//) {
//    var state by remember { mutableStateOf(CLOSED) }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
//    ) {
//        PochakTopAppBar(
//            leftContent = { BackButton(onClick = onBack) },
//            centerContent = {
//                Text(
//                    text = postDetail.ownerHandle + stringResource(id = R.string.feature_post_detail_title_suffix),
//                    style = MaterialTheme.typography.titleMedium
//                )
//            },
//            rightContent = {
//                MoreButton { state = MORE }
//            }
//        )
//
//        MemberItemInPost(
//            modifier = Modifier.padding(horizontal = HorizontalPadding, vertical = 8.dp),
//            imageUrl = postDetail.ownerProfileImage,
//            tagList = postDetail.tagList.map { it.handle },
//            handle = postDetail.ownerHandle,
//            onClickItem = { state = TAG },
////            onClickImage = { },
//        ) {
//            // 자신의 게시물이 아닐경우, 팔로우/팔로잉 버튼 표시
//            postDetail.isFollow?.let {
//                FollowButton(
//                    isFollow = it,
//                    onClick = { /* 팔로우/팔로잉 버튼 클릭 시 동작 */ }
//                )
//            }
//        }
//
//        // 게시물 이미지
//        AsyncImage(
//            model = postDetail.postImage,
//            contentDescription = "Post Image",
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = HorizontalPadding, vertical = 8.dp),
//            placeholder = painterResource(id = R.drawable.feature_post_detail_placeholder),
//        )
//
//        // 캡션, 좋아요, 댓글
//        CaptionAndIcons(
//            postDetail = postDetail,
//            onClickLike = onClickLike,
//            onClickComment = { state = COMMENT }
//        )
//
//        // 최근 댓글
//        postDetail.recentComment?.let {
//            RecentComment(
//                recentComment = it,
//                onClickComment = { state = COMMENT }
//            )
//        }
//    }
//
//    BottomSheetContent(
//        state = state,
//        postDetail = postDetail,
//        onDismiss = { state = CLOSED },
//        changeState = { state = it }
//    )
//}
//
//@Composable
//private fun CaptionAndIcons(
//    postDetail: NetworkPostDetail,
//    onClickLike: () -> Unit,
//    onClickComment: () -> Unit,
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = HorizontalPadding, end = 10.dp, top = 8.dp, bottom = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Row(
//            modifier = Modifier.weight(1f),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Text(
//                text = postDetail.ownerHandle,
//                style = PochakTextStyle.body3_1,
//            )
//
//            Text(
//                text = postDetail.caption,
//                style = PochakTextStyle.body3,
//            )
//        }
//
//        Row {
//            IconButton(onClick = onClickLike) {
//                Image(
//                    painter = painterResource(if (postDetail.isLike) PochakIcons.HeartFilled else PochakIcons.Heart),
//                    contentDescription = "Like"
//                )
//            }
//            IconButton(onClick = onClickComment) {
//                Image(
//                    painter = painterResource(if (postDetail.recentComment != null) PochakIcons.CommentFilled else PochakIcons.Comment),
//                    contentDescription = "Comment"
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun RecentComment(
//    recentComment: NetworkComment,
//    onClickComment: () -> Unit,
//) {
//    HorizontalDivider(
//        modifier = Modifier.padding(horizontal = HorizontalPadding),
//        thickness = 1.dp,
//        color = Gray01
//    )
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = HorizontalPadding, vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(vertical = 8.dp)
//                .weight(1f),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Text(
//                text = recentComment.handle,
//                style = PochakTextStyle.body3_1,
//            )
//
//            Text(
//                text = recentComment.content,
//                style = PochakTextStyle.body3,
//                overflow = TextOverflow.Ellipsis,
//            )
//        }
//
//        Text(
//            text = "더보기",
//            style = PochakTextStyle.body3,
//            color = Gray05,
//            modifier = Modifier.clickable(onClick = onClickComment)
//        )
//    }
//}
//
//enum class PostDetailBottomSheetState(
//    val title: String
//) {
//    CLOSED(""),
//    TAG("태그"),
//    MORE("더보기"),
//    REPORT("신고하기"),
//    COMMENT(" 님의 게시물 댓글"),
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun BottomSheetContent(
//    state: PostDetailBottomSheetState,
//    postDetail: NetworkPostDetail,
//    onDismiss: () -> Unit,
//    changeState: (PostDetailBottomSheetState) -> Unit,
//) {
//    val sheetState = rememberModalBottomSheetState()
//    val scope = rememberCoroutineScope()
//
//    fun hideSheet() {
//        scope.launch { sheetState.hide() }
//            .invokeOnCompletion {
//                if (!sheetState.isVisible) {
//                    changeState(CLOSED)
//                }
//            }
//    }
//
//    if (state != CLOSED) {
//        ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Text(
//                    text = if (state == COMMENT) postDetail.ownerHandle + state.title else state.title,
//                    style = PochakTextStyle.body0
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                when (state) {
//                    MORE -> MoreContent(
//                        modifier = Modifier.padding(horizontal = HorizontalPadding),
//                        isOwner = postDetail.isFollow == null,
//                        onClickReport = {
//                            scope.launch { sheetState.hide() }
//                                .invokeOnCompletion {
//                                    if (!sheetState.isVisible) {
//                                        changeState(REPORT)
//                                        scope.launch { sheetState.show() }
//                                    }
//                                }
//                        },
//                        onClickCancel = { hideSheet() },
//                    )
//
//                    REPORT -> {
//                        val reportList = listOf(
//                            stringResource(id = R.string.feature_post_detail_report_dislike),
//                            stringResource(id = R.string.feature_post_detail_report_spam),
//                            stringResource(id = R.string.feature_post_detail_report_nudity),
//                            stringResource(id = R.string.feature_post_detail_report_fraud),
//                            stringResource(id = R.string.feature_post_detail_report_violence),
//                            stringResource(id = R.string.feature_post_detail_report_false_information),
//                        )
//
//                        reportList.forEach { report ->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        hideSheet()
//                                    }
//                                    .padding(start = HorizontalPadding),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically,
//                            ) {
//                                Text(
//                                    modifier = Modifier.padding(vertical = 14.dp),
//                                    text = report,
//                                    style = PochakTextStyle.body2
//                                )
//
//                                Box(
//                                    modifier = Modifier.size(48.dp),
//                                    contentAlignment = Alignment.Center,
//                                ) {
//                                    Image(
//                                        painter = painterResource(PochakIcons.ArrowRight),
//                                        contentDescription = null
//                                    )
//                                }
//                            }
//
//                            if (report != reportList.last()) {
//                                HorizontalDivider(color = Gray01)
//                            }
//                        }
//                    }
//
//                    TAG -> {
//                        Column {
//                            postDetail.tagList.forEach { tag ->
//                                MemberItem(
//                                    modifier = Modifier.padding(vertical = 12.dp),
//                                    imageUrl = tag.profileImage,
//                                    imageSize = 40.dp,
//                                    title = tag.handle,
//                                    text = tag.name,
//                                )
//
//                                if (tag != postDetail.tagList.last()) {
//                                    HorizontalDivider(color = Gray01)
//                                }
//                            }
//                        }
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun MoreContent(
//    modifier: Modifier = Modifier,
//    isOwner: Boolean,
//    onClickReport: () -> Unit,
//    onClickDelete: () -> Unit = { },
//    onClickCancel: () -> Unit,
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp),
//    ) {
//        TextWithIcon(
//            modifier = Modifier.clickable(onClick = onClickReport),
//            icon = PochakIcons.Report,
//            text = "신고하기",
//        )
//
//        HorizontalDivider(color = Gray01)
//
//        if (isOwner) {
//            TextWithIcon(
//                modifier = Modifier.clickable(onClick = onClickDelete),
//                icon = PochakIcons.DeleteBin,
//                text = "삭제하기",
//                color = ErrorColor
//            )
//
//            HorizontalDivider(color = Gray01)
//        }
//
//        TextWithIcon(
//            modifier = Modifier.clickable(onClick = onClickCancel),
//            icon = PochakIcons.Cancel,
//            text = "취소",
//        )
//    }
//}
//
//@Composable
//private fun TextWithIcon(
//    modifier: Modifier = Modifier,
//    icon: Int,
//    text: String,
//    color: Color = Color.Black
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Image(
//            painter = painterResource(id = icon),
//            contentDescription = null,
//        )
//
//        Text(
//            text = text,
//            style = PochakTextStyle.body2,
//            color = color
//        )
//    }
//}
//
////@Preview
////@Composable
////private fun PostDetailScreenPreview() {
////    PochakTheme {
////        PostDetailScreen(
////            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
////            onBack = { },
////            uiState = PostDetailUiState.Success(
////                NetworkPostDetail(
////                    ownerHandle = "ownerHandle",
////                    ownerProfileImage = "https://www.example.com/image.jpg",
////                    postImage = "https://www.example.com/image.jpg",
////                    caption = "caption1 caption1 caption1 caption1\ncaption2",
////                    tagList = listOf(
////                        NetworkTag(
////                            memberId = 1,
////                            handle = "tagHandle",
////                            name = "tagName",
////                            profileImage = "https://www.example.com/image.jpg"
////                        )
////                    ),
////                    isLike = true,
////                    recentComment = NetworkComment(
////                        commentId = 1,
////                        memberId = 1,
////                        profileImage = "https://www.example.com/image.jpg",
////                        handle = "commentHandle",
////                        createdDate = "2021-01-01",
////                        content = "content content content content",
////                    ),
////                    ownerId = 1,
////                    isFollow = false,
////                    likeCount = 0,
////                )
////            ),
////            onClickLike = { }
////        )
////    }
////}

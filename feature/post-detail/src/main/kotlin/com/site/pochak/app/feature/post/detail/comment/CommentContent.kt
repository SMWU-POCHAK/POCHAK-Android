package com.site.pochak.app.feature.post.detail.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.RoundedButton
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.network.model.NetworkCommentWithChild

@Composable
internal fun CommentContent(
    modifier: Modifier = Modifier,
    viewModel: CommentViewModel = hiltViewModel()
) {
    val commentList by viewModel.commentList.collectAsStateWithLifecycle()
    val myProfileImageUrl = viewModel.myProfileImageUrl

    var parentComment by rememberSaveable { mutableStateOf<NetworkCommentWithChild?>(null) }

    Column(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                // imePadding을 위해 weight를 1로 설정.
                // 이때, LazyColumn의 height는 wrap_content로 설정해야 함.
                // LazyColumn의 height를 fillMaxSize로 설정하면, ModalBottomSheet 전체 높이를 차지함.
                .weight(1f, false),
            contentPadding = PaddingValues(horizontal = HorizontalPadding, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(count = commentList.size, key = { commentList[it].commentId }) {
                val comment = commentList[it]

                CommentItem(
                    imageUrl = comment.profileImage,
                    handle = comment.handle,
                    content = comment.content,
                    isOwner = comment.profileImage == myProfileImageUrl,
                    onClickReply = { parentComment = comment },
                    onClickDelete = { viewModel.deleteComment(comment.commentId) },
                ) {
                    if (comment.childCommentList.isNotEmpty()) {
                        comment.childCommentList.forEach { childComment ->
                            Spacer(modifier = Modifier.height(16.dp))

                            CommentItem(
                                imageUrl = childComment.profileImage,
                                imageSize = 36.dp,
                                handle = childComment.handle,
                                content = childComment.content,
                                isOwner = childComment.profileImage == myProfileImageUrl,
                                onClickDelete = { viewModel.deleteComment(childComment.commentId) },
                                isChild = true,
                            )
                        }
                    }
                }
            }

            if (commentList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 108.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(id = PochakIcons.ChatEmpty),
                            contentDescription = null,
                        )

                        Text(
                            text = "게시물 댓글이 없습니다.",
                            style = PochakTextStyle.body3,
                            color = Gray04,
                        )
                    }
                }
            }
        }

        CommentTextField(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            myProfileImageUrl = myProfileImageUrl,
            parentComment = parentComment,
            uploadComment = { comment, parentCommentId ->
                viewModel.upLoadComment(comment, parentCommentId)
            },
            removeParentComment = { parentComment = null },
        )
    }
}

@Composable
private fun CommentItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    imageSize: Dp = 40.dp,
    handle: String,
    content: String,
    isChild: Boolean = false,
    isOwner: Boolean = false,
    onClickReply: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    childContent: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleCropAsyncImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.Top),
        )

        Column {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = handle,
                    style = PochakTextStyle.body3_1,
                )

                Text(
                    text = content,
                    style = PochakTextStyle.body3,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (!isChild) {
                        Text(
                            text = "답글 달기",
                            style = PochakTextStyle.caption2,
                            color = Gray05,
                            modifier = Modifier.clickable { onClickReply() }
                        )
                    }

                    if (isOwner) {
                        Text(
                            text = "삭제",
                            style = PochakTextStyle.caption2,
                            color = Gray05,
                            modifier = Modifier.clickable { onClickDelete() }
                        )
                    }
                }
            }

            childContent()
        }
    }
}

@Composable
internal fun CommentTextField(
    modifier: Modifier = Modifier,
    myProfileImageUrl: String,
    parentComment: NetworkCommentWithChild?,
    uploadComment: (String, Int?) -> Unit,
    removeParentComment: () -> Unit,
) {
    var myComment by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier) {
        parentComment?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray01)
                    .padding(horizontal = HorizontalPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${it.handle}님에게 남기는 답글",
                    style = PochakTextStyle.body3_1,
                )

                Image(
                    painter = painterResource(id = PochakIcons.Cancel),
                    contentDescription = null,
                    modifier = Modifier.clickable { removeParentComment() },
                )
            }

            HorizontalDivider(color = Gray03)
        }

        Row(
            modifier = Modifier
                .background(Gray01)
                .padding(horizontal = HorizontalPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCropAsyncImage(
                imageUrl = myProfileImageUrl,
                modifier = Modifier.size(40.dp),
            )

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = myComment,
                    onValueChange = { myComment = it },
                    textStyle = PochakTextStyle.body2,
                    singleLine = true,
                )

                if (myComment.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "이 게시물에 댓글을 달아보세요",
                        style = PochakTextStyle.body2,
                        color = Gray03,
                    )
                }
            }

            RoundedButton(
                text = "업로드",
                onClick = {
                    uploadComment(myComment, parentComment?.commentId)
                    myComment = ""
                },
                textStyle = PochakTextStyle.body3_1,
            )
        }
    }
}
package com.site.pochak.app.feature.post.detail.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachReversed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.drawTopBorder
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.network.model.ChildCommentPageResponse
import com.site.pochak.app.core.ui.ElapsedTimeText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val backgroundColor = Color(0xFFFFF1D8)

@Composable
internal fun CommentContent(
    modifier: Modifier = Modifier,
    viewModel: CommentViewModel = hiltViewModel(),
    ownerHandle: String,
) {
    val myProfileImageUrl by viewModel.loginMemberProfileImage.collectAsStateWithLifecycle()
    val commentList = viewModel.commentList.collectAsLazyPagingItems()
    var parentComment by remember { mutableStateOf<ChildCommentPageResponse?>(null) }

    val scope = rememberCoroutineScope()
    fun refreshCommentList() {
        scope.launch {
            delay(1000L)
            commentList.refresh()
        }
    }

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
            reverseLayout = true,
        ) {
            items(count = commentList.itemCount) {
                val comment = commentList[it] ?: return@items

                CommentItem(
                    imageUrl = comment.profileImage,
                    handle = comment.handle,
                    content = comment.content,
                    isOwner = comment.profileImage == myProfileImageUrl,
                    isSelected = parentComment == comment,
                    onClickReply = { parentComment = comment },
                    onClickDelete = { viewModel.deleteComment(comment.commentId) },
                    createdDate = comment.createdDate,
                ) {
                    val childComments = remember { viewModel.childComments[comment.commentId] }
                    val isLast by viewModel.childCommentPageIsLast[comment.commentId]?.collectAsStateWithLifecycle()
                        ?: remember { mutableStateOf(false) }

                    if (!isLast && !comment.childCommentPageInfo.lastPage) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = HorizontalPadding,
                                vertical = 12.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.width(20.dp),
                            )

                            Text(
                                text = "이전 답글 보기",
                                style = PochakTextStyle.caption2,
                                color = Gray05,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.loadChildComments(comment.commentId)
                                    }
                            )
                        }
                    }

                    if (childComments?.isEmpty() == true) {
                        comment.childCommentList.forEach { childComment ->
                            CommentItem(
                                imageUrl = childComment.profileImage,
                                imageSize = 36.dp,
                                handle = childComment.handle,
                                content = childComment.content,
                                isOwner = childComment.profileImage == myProfileImageUrl,
                                onClickDelete = { viewModel.deleteComment(childComment.commentId) },
                                isChild = true,
                                createdDate = childComment.createdDate,
                            )
                        }
                    }

                    childComments?.fastForEachReversed { childComment ->
                        CommentItem(
                            imageUrl = childComment.profileImage,
                            imageSize = 36.dp,
                            handle = childComment.handle,
                            content = childComment.content,
                            isOwner = childComment.profileImage == myProfileImageUrl,
                            onClickDelete = { viewModel.deleteComment(childComment.commentId) },
                            isChild = true,
                            createdDate = childComment.createdDate,
                        )
                    }
                }
            }

            if (commentList.itemCount == 0) {
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
            ownerHandle = ownerHandle,
            myProfileImageUrl = myProfileImageUrl,
            parentComment = parentComment,
            uploadComment = { comment, parentCommentId ->
                viewModel.uploadComment(comment, parentCommentId)
                refreshCommentList()
            },
            removeParentComment = {
                parentComment = null
            },
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
    isSelected: Boolean = false,
    createdDate: String?,
    onClickReply: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    childContent: @Composable () -> Unit = {},
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = if (isSelected) backgroundColor else Color.Transparent,
                )
                .padding(horizontal = HorizontalPadding, vertical = 12.dp),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = handle,
                            style = PochakTextStyle.body3_1,
                        )

                        ElapsedTimeText(
                            createdDate = createdDate ?: "",
                        )
                    }

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
            }
        }


        Column(
            // 이미지 크기 + 여백
            modifier = Modifier.padding(start = imageSize + 12.dp)
        ) {
            childContent()
        }
    }
}

@Composable
internal fun CommentTextField(
    modifier: Modifier = Modifier,
    ownerHandle: String,
    myProfileImageUrl: String,
    parentComment: ChildCommentPageResponse?,
    uploadComment: (String, Int?) -> Unit,
    removeParentComment: () -> Unit,
) {
    var myComment by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.drawTopBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = HorizontalPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCropAsyncImage(
                imageUrl = myProfileImageUrl,
                modifier = Modifier.size(40.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFC7CDD2),
                        shape = RoundedCornerShape(size = 15.dp),
                    )
                    .clip(RoundedCornerShape(size = 15.dp)),
            ) {
                parentComment?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "@${it.handle}님에게 답글 남기는 중",
                            style = PochakTextStyle.caption2,
                        )

                        Image(
                            painter = painterResource(id = PochakIcons.Cancel),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { removeParentComment() },
                        )
                    }

                    HorizontalDivider(color = Gray03)
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = myComment,
                            onValueChange = { myComment = it },
                            textStyle = PochakTextStyle.body3,
                            singleLine = true,
                        )

                        if (myComment.isEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "@${ownerHandle}님에게 댓글 달기",
                                style = PochakTextStyle.body3,
                                color = Gray03,
                            )
                        }
                    }

                    Image(
                        painter = painterResource(id = PochakIcons.UploadComment),
                        contentDescription = "Upload Comment",
                        modifier = Modifier.clickable {
                            uploadComment(myComment, parentComment?.commentId)
                            myComment = ""
                            removeParentComment()
                        }
                    )
                }
            }
        }
    }
}
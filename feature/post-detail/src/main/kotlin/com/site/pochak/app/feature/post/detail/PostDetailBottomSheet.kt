package com.site.pochak.app.feature.post.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.ErrorColor
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.network.model.NetworkPostDetail
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.CLOSED
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.COMMENT
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.MORE
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.REPORT
import com.site.pochak.app.feature.post.detail.PostDetailBottomSheetState.TAG
import com.site.pochak.app.feature.post.detail.comment.CommentContent
import kotlinx.coroutines.launch

internal enum class PostDetailBottomSheetState(
    @StringRes val title: Int,
) {
    CLOSED(R.string.feature_post_detail_bottom_sheet_closed),
    TAG(R.string.feature_post_detail_bottom_sheet_tag),
    MORE(R.string.feature_post_detail_bottom_sheet_more),
    REPORT(R.string.feature_post_detail_bottom_sheet_report),
    COMMENT(R.string.feature_post_detail_bottom_sheet_comment),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostDetailBottomSheetContent(
    state: PostDetailBottomSheetState,
    postDetail: NetworkPostDetail,
    onDismiss: () -> Unit,
    changeState: (PostDetailBottomSheetState) -> Unit,
    onDelete: () -> Unit,
    reportPost: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun hideSheet() {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
    }

    if (state != CLOSED) {
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (state == COMMENT) postDetail.ownerHandle + stringResource(id = state.title)
                    else stringResource(id = state.title),
                    style = PochakTextStyle.body0
                )

                when (state) {
                    MORE -> MoreContent(
                        modifier = Modifier.padding(horizontal = HorizontalPadding),
                        isOwner = postDetail.isFollow == null,
                        onClickReport = {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        changeState(REPORT)
                                        scope.launch { sheetState.show() }
                                    }
                                }
                        },
                        onClickCancel = { hideSheet() },
                        onClickDelete = onDelete,
                    )

                    REPORT -> {
                        val reportList = mapOf(
                            "NOT_INTERESTED" to stringResource(id = R.string.feature_post_detail_report_not_interested),
                            "SPAM" to stringResource(id = R.string.feature_post_detail_report_spam),
                            "NUDITY_OR_SEXUAL_CONTENT" to stringResource(id = R.string.feature_post_detail_report_nudity_or_sexual_content),
                            "FRAUD_OR_SCAM" to stringResource(id = R.string.feature_post_detail_report_fraud_or_scam),
                            "HATE_SPEECH_OR_SYMBOL" to stringResource(id = R.string.feature_post_detail_report_hate_speech_or_symbol),
                            "MISINFORMATION" to stringResource(id = R.string.feature_post_detail_report_misinformation),
                        )

                        reportList.forEach { report ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        reportPost(report.key)
                                        hideSheet()
                                    }
                                    .padding(start = HorizontalPadding),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    text = report.value,
                                    style = PochakTextStyle.body2
                                )

                                Box(
                                    modifier = Modifier.size(48.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = painterResource(PochakIcons.ArrowRight),
                                        contentDescription = null
                                    )
                                }
                            }

                            if (report != reportList.entries.last()) {
                                HorizontalDivider(color = Gray01)
                            }
                        }
                    }

                    TAG -> {
                        Column {
                            postDetail.tagList.forEach { tag ->
                                MemberItem(
                                    imageUrl = tag.profileImage,
                                    title = tag.handle,
                                    text = tag.name,
                                    onClickItem = { /* 프로필로 이동 */ }
                                )

                                if (tag != postDetail.tagList.last()) {
                                    HorizontalDivider(color = Gray01)
                                }
                            }
                        }
                    }

                    COMMENT -> {
                        CommentContent()
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun MoreContent(
    modifier: Modifier = Modifier,
    isOwner: Boolean,
    onClickReport: () -> Unit,
    onClickDelete: () -> Unit,
    onClickCancel: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        TextWithIcon(
            modifier = Modifier.clickable(onClick = onClickReport),
            icon = PochakIcons.Report,
            text = "신고하기",
        )

        HorizontalDivider(color = Gray01)

        if (isOwner) {
            TextWithIcon(
                modifier = Modifier.clickable(onClick = onClickDelete),
                icon = PochakIcons.DeleteBin,
                text = "삭제하기",
                color = ErrorColor
            )

            HorizontalDivider(color = Gray01)
        }

        TextWithIcon(
            modifier = Modifier.clickable(onClick = onClickCancel),
            icon = PochakIcons.Cancel,
            text = "취소",
        )
    }
}

@Composable
private fun TextWithIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    color: Color = Color.Black
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
        )

        Text(
            text = text,
            style = PochakTextStyle.body2,
            color = color
        )
    }
}

@Composable
private fun MemberItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    text: String,
    onClickItem: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding)
            .clickable(onClick = onClickItem)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleCropAsyncImage(
            modifier = Modifier.size(40.dp),
            imageUrl = imageUrl,
            contentDescription = "profile image",
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = PochakTextStyle.body3_1,
            )

            Text(
                text = text,
                style = PochakTextStyle.body3,
            )
        }
    }
}
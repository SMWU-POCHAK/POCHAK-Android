package com.site.pochak.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.FollowButton
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.RoundedButton
import com.site.pochak.app.core.designsystem.component.noRippleClickable
import com.site.pochak.app.core.designsystem.theme.PochakTheme

@Composable
fun MemberItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    imageSize: Dp = 50.dp,
    title: String? = null,
    titleFontStyle: TextStyle = PochakTextStyle.body1,
    text: String? = null,
    textFontStyle: TextStyle = PochakTextStyle.body4,
    onClickItem: () -> Unit = {},
    onClickImage: () -> Unit = {},
    rightContent: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClickItem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCropAsyncImage(
                modifier = Modifier.size(imageSize),
                imageUrl = imageUrl,
                contentDescription = "profile image",
                onClick = onClickImage,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = titleFontStyle,
                    )
                }

                text?.let {
                    Text(
                        text = it,
                        style = textFontStyle,
                    )
                }
            }
        }

        rightContent()
    }
}

@Composable
fun MemberItemInPost(
    modifier: Modifier = Modifier,
    imageUrl: String,
    imageSize: Dp = 50.dp,
    tagList: List<String>,
    maxTagCount: Int = 2,
    handle: String,
    onClickItem: () -> Unit = {},
    onClickImage: () -> Unit = {},
    rightContent: @Composable () -> Unit = {},
) {
    MemberItem(
        modifier = modifier,
        imageUrl = imageUrl,
        imageSize = imageSize,
        title = tagList.take(maxTagCount)
            .joinToString(" · ") { it + "님" } + if (tagList.size > maxTagCount) "..." else "",
        text = handle + "님이 포착",
        titleFontStyle = MaterialTheme.typography.titleSmall,
        textFontStyle = MaterialTheme.typography.bodyMedium,
        onClickItem = onClickItem,
        onClickImage = onClickImage,
        rightContent = rightContent,
    )
}

@Preview
@Composable
private fun MemberItemPreview() {
    PochakTheme {
        MemberItem(
            imageUrl = "",
            title = "Float2_y",
            text = "이름",
            modifier = Modifier.background(Color.White)
        )
    }
}

@Preview
@Composable
private fun MemberItemWithoutTitlePreview() {
    PochakTheme {
        MemberItem(
            imageUrl = "",
            text = "준수 님이 회원님을 포착했습니다.",
            modifier = Modifier.background(Color.White)
        ) {
            RoundedButton(text = "미리보기")
        }
    }
}

@Preview
@Composable
private fun MemberItemInPostUnfollowPreview() {
    PochakTheme {
        MemberItemInPost(
            imageUrl = "",
            tagList = listOf("Jal", "jyle_kim"),
            handle = "Kyle",
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            rightContent = { FollowButton(isFollow = false) }
        )
    }
}

@Preview
@Composable
private fun MemberItemInPostFollowPreview() {
    PochakTheme {
        MemberItemInPost(
            imageUrl = "",
            tagList = listOf("cyndiin", "su.yeonn_", "su.yeonn_"),
            handle = "Kyle",
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            rightContent = { FollowButton(isFollow = true) }
        )
    }
}
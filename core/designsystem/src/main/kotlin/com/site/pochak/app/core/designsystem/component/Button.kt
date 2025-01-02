package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.PochakTheme
import com.site.pochak.app.core.designsystem.theme.Yellow00

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    paddingValues: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    text: String,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(18.dp),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(roundedCornerShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(paddingValues),
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor,
        )
    }
}

@Composable
fun FollowButton(
    modifier: Modifier = Modifier,
    isFollow: Boolean,
    paddingValues: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    onClick: () -> Unit = {},
) {
    RoundedButton(
        modifier = modifier,
        text = if (isFollow) "팔로잉" else "팔로우",
        paddingValues = paddingValues,
        backgroundColor = if (isFollow) Gray04 else MaterialTheme.colorScheme.primary,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun RoundedButtonPreview() {
    PochakTheme {
        RoundedButton(
            text = "버튼",
        )
    }
}

@Preview
@Composable
private fun FollowSmallButtonPreviewUnFollowed() {
    PochakTheme {
        FollowButton(isFollow = false)
    }
}

@Preview
@Composable
private fun FollowSmallButtonPreviewFollowed() {
    PochakTheme {
        FollowButton(isFollow = true)
    }
}
package com.site.pochak.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.site.pochak.app.core.designsystem.component.TextFieldWithHint
import com.site.pochak.app.core.designsystem.theme.Yellow00

@Composable
fun ProfileInfoScreen(
    profileInfo: ProfileInfo
) {
    val stringList = remember { mutableStateListOf("", "", "") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.core_ui_profile_info_add_image),
            contentDescription = "Add Profile Image"
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ProfileInfoTextField(
                title = stringResource(id = R.string.core_ui_profile_name_title),
                value = nameText,
                onValueChange = { nameText = it },
                hintText = stringResource(id = R.string.core_ui_profile_name_hint)
            )

            ProfileInfoTextField(
                title = stringResource(id = R.string.core_ui_profile_handle_title),
                value = handleText,
                onValueChange = { handleText = it },
                hintText = stringResource(id = R.string.core_ui_profile_handle_hint)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.core_ui_profile_check_duplicate),
                    contentDescription = "Check Handle Icon"
                )
            }

            ProfileInfoTextField(
                title = stringResource(id = R.string.core_ui_profile_message_title),
                value = messageText,
                onValueChange = { messageText = it },
                hintText = stringResource(id = R.string.core_ui_profile_message_hint)
            )
        }
    }
}

@Composable
private fun ProfileInfoTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String,
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(2f),
        )

        Row(modifier = Modifier.weight(8f)) {
            TextFieldWithHint(
                value = value,
                onValueChange = onValueChange,
                hintText = hintText,
                modifier = Modifier.weight(1f)
            )

            content()
        }
    }
}

@Preview
@Composable
private fun ProfileInfoTextFieldPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        ProfileInfoTextField(
            title = "이름",
            value = "",
            onValueChange = {},
            hintText = "이름을 입력해주세요."
        )
    }
}

@Preview
@Composable
private fun ProfileInfoTextFieldFillPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        ProfileInfoTextField(
            title = "이름",
            value = "홍길동",
            onValueChange = {},
            hintText = "이름을 입력해주세요."
        )
    }
}

@Preview
@Composable
private fun ProfileInfoTextFieldPreviewWithContent() {
    Box(modifier = Modifier.background(Color.White)) {
        ProfileInfoTextField(
            title = "닉네임",
            value = "",
            onValueChange = {},
            hintText = "닉네임을 입력해주세요."
        ) {
            Text(
                text = "중복확인",
                style = MaterialTheme.typography.bodySmall,
                color = Yellow00
            )
        }
    }
}
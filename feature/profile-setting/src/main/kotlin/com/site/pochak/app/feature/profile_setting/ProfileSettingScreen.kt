package com.site.pochak.app.feature.profile_setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.TextFieldWithHint
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Yellow00

@Composable
internal fun ProfileSettingRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileSettingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    val checkHandleUiState by viewModel.checkHandleUiState.collectAsStateWithLifecycle()

    ProfileSettingScreen(
        modifier = modifier,
        checkHandleUiState = checkHandleUiState,
        checkDuplicateHandle = viewModel::checkDuplicateHandle,
        resetCheckHandleUiState = viewModel::resetCheckHandleUiState,
        navigateToHome = navigateToHome,
        onBackClick = onBackClick,
        onComplete = viewModel::onComplete
    )
}

@Composable
internal fun ProfileSettingScreen(
    modifier: Modifier = Modifier,
    checkHandleUiState: CheckHandleUiState,
    checkDuplicateHandle: (String) -> Unit,
    resetCheckHandleUiState: () -> Unit,
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
    onComplete: (String, String, String) -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var handleText by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    // 완료 버튼 활성화 여부
    var actionEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(nameText, handleText, messageText, checkHandleUiState) {
        // 입력값이 모두 채워져 있고, 닉네임 중복 체크가 성공한 경우에만 완료 버튼 활성화
        actionEnabled =
            (nameText.trim() != "" && handleText.trim() != "" && messageText.trim() != "")
                    && checkHandleUiState is CheckHandleUiState.Success
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        PochakTopAppBar(
            title = { Text(text = stringResource(id = R.string.feature_profile_setting_title)) },
            showNavigationIcon = true,
            showActionIcon = true,
            actionIcon = {
                IconButton({
                    if (actionEnabled) {
                        onComplete(nameText, handleText, messageText)
                    }
                }) {
                    Text(
                        text = stringResource(id = R.string.feature_profile_setting_save),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (actionEnabled) Yellow00 else Gray03,
                    )
                }
            },
            onNavigationClick = { onBackClick() }
        )

        Image(
            painter = painterResource(id = R.drawable.feature_profile_setting_info_add_image),
            contentDescription = "Add Profile Image"
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ProfileInfoTextField(
                title = stringResource(id = R.string.feature_profile_setting_name_title),
                value = nameText,
                onValueChange = { nameText = it },
                hintText = stringResource(id = R.string.feature_profile_setting_name_hint)
            )

            ProfileInfoTextField(
                title = stringResource(id = R.string.feature_profile_setting_handle_title),
                value = handleText,
                onValueChange = {
                    handleText = it
                    // 닉네임 중복 체크를 다시 하기 위해 상태 초기화
                    resetCheckHandleUiState()
                },
                hintText = stringResource(id = R.string.feature_profile_setting_handle_hint)
            ) {

                Image(
                    painter = painterResource(
                        if (checkHandleUiState is CheckHandleUiState.Success)
                            R.drawable.feature_profile_setting_duplicate_checked
                        else
                            R.drawable.feature_profile_setting_duplicate_unchecked
                    ),
                    contentDescription = "Check Handle Icon",
                    modifier = Modifier.clickable {
                        checkDuplicateHandle(handleText)
                    }
                )
            }

            ProfileInfoTextField(
                title = stringResource(id = R.string.feature_profile_setting_message_title),
                value = messageText,
                onValueChange = { messageText = it },
                hintText = stringResource(id = R.string.feature_profile_setting_message_hint)
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
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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

        HorizontalDivider(color = Gray01)
    }
}

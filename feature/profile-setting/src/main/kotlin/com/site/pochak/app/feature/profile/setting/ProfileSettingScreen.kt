package com.site.pochak.app.feature.profile.setting

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.Yellow01

const val TAG = "ProfileSettingScreen"

@Composable
fun ProfileSettingRoute(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileSettingViewModel = hiltViewModel(),
) {
    val checkHandleUiState by viewModel.checkHandleUiState.collectAsStateWithLifecycle()

    ProfileSettingScreen(
        modifier = modifier,
        navigateToHome = navigateToHome,
        onBack = onBack,
        checkHandleUiState = checkHandleUiState,
        checkHandle = viewModel::checkHandle,
        resetCheckHandle = viewModel::resetCheckHandleUiState,
    )
}

@Composable
internal fun ProfileSettingScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBack: () -> Unit,
    checkHandleUiState: CheckHandleUiState,
    checkHandle: (String) -> Unit,
    resetCheckHandle: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (checkHandleUiState is CheckHandleUiState.Loading) {
            CircularProgressIndicator()
        }

        ProfileSettingContent(
            modifier = modifier,
            onBack = onBack,
            checkHandleUiState = checkHandleUiState,
            checkHandle = checkHandle,
            resetCheckHandle = resetCheckHandle,
        )
    }
}

@Composable
private fun ProfileSettingContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    checkHandleUiState: CheckHandleUiState,
    checkHandle: (String) -> Unit,
    resetCheckHandle: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var handle by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val profileInputsState by remember {
        derivedStateOf { listOf(name, handle, message) }
    }

    // 완료 버튼 활성화 여부
    var actionEnabled by remember { mutableStateOf(false) }

    // 입력값이 모두 채워져 있고, 닉네임 중복 체크가 성공한 경우에만 완료 버튼 활성화
    LaunchedEffect(profileInputsState, checkHandleUiState) {
        actionEnabled =
            profileInputsState.all { it.isNotEmpty() }
                    && checkHandleUiState is CheckHandleUiState.Checked
    }

    // handle 변경 시, 다시 체크
    LaunchedEffect(handle) {
        if (checkHandleUiState is CheckHandleUiState.Checked) {
            resetCheckHandle()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PochakTopAppBar(
            leftContent = { BackButton(onClick = onBack) },
            centerContent = { Text(text = stringResource(R.string.feature_profile_setting_title)) },
            rightContent = {
                IconButton(onClick = {}) {
                    Text(
                        text = stringResource(R.string.feature_profile_setting_save),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (actionEnabled) Yellow01 else Gray03,
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = HorizontalPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.feature_profile_setting_add_profile),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(40.dp))

            ProfileInputField(
                title = R.string.feature_profile_setting_name_title,
                hint = R.string.feature_profile_setting_name_hint,
                regexText = R.string.feature_profile_setting_name_regex,
                regex = Regex(".{0,15}$"),
                value = name,
                onValueChange = { name = it },
            )

            ProfileInputField(
                title = R.string.feature_profile_setting_handle_title,
                hint = R.string.feature_profile_setting_handle_hint,
                regexText = R.string.feature_profile_setting_handle_regex,
                regex = Regex("^[a-zA-Z0-9._]{0,15}\$"),
                value = handle,
                onValueChange = { handle = it },
            ) {
                // 닉네임 중복 체크 버튼
                Image(
                    painter = painterResource(
                        if (checkHandleUiState is CheckHandleUiState.Checked) {
                            R.drawable.feature_profile_setting_handle_checked
                        } else {
                            R.drawable.feature_profile_setting_handle_unchecked
                        }
                    ),
                    contentDescription = "Handle Check",
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (checkHandleUiState is CheckHandleUiState.UnChecked && handle.isNotEmpty()) {
                                checkHandle(handle)
                            }
                        }
                )
            }

            ProfileInputField(
                title = R.string.feature_profile_setting_message_title,
                hint = R.string.feature_profile_setting_message_hint,
                regexText = R.string.feature_profile_setting_message_regex,
                regex = Regex("^(?:[^\\n]*\\n?){0,2}[^\\n]{0,50}\$"),
                value = message,
                onValueChange = { message = it },
                singleLine = false,
            )
        }
    }
}

@Composable
private fun ProfileInputField(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes hint: Int,
    @StringRes regexText: Int,
    regex: Regex,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(3f),
            )

            Row(
                modifier = Modifier.weight(7f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = {
                            if (regex.matches(it) && it.firstOrNull() != ' ') {
                                onValueChange(it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = singleLine,
                    )

                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray03,
                        )
                    }
                }

                content()
            }
        }

        // 입력 포맷 안내문
        Text(
            text = stringResource(regexText),
            style = MaterialTheme.typography.bodyMedium,
            color = Gray04,
        )

        HorizontalDivider(color = Gray01)
    }
}
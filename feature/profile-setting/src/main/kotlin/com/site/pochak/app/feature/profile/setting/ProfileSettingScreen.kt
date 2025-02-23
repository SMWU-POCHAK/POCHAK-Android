package com.site.pochak.app.feature.profile.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.site.pochak.app.core.data.uriToFile
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakAlertDialog
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.ErrorColor
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.PositiveColor
import com.site.pochak.app.core.designsystem.theme.Yellow00
import kotlinx.coroutines.delay
import java.io.File

private const val TAG = "ProfileSettingScreen"

@Composable
fun ProfileSettingRoute(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileSettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checkHandleState by viewModel.checkHandleState.collectAsStateWithLifecycle()
    val signUpState by viewModel.signUpState.collectAsStateWithLifecycle()
    val updateProfileState by viewModel.updateProfileState.collectAsStateWithLifecycle()

    ProfileSettingScreen(
        modifier = modifier,
        navigateToHome = navigateToHome,
        onBack = onBack,
        uiState = uiState,
        checkHandleState = checkHandleState,
        checkHandle = viewModel::checkHandle,
        resetCheckHandle = viewModel::resetCheckHandleState,
        signUp = viewModel::signUp,
        signUpState = signUpState,
        updateProfile = viewModel::updateProfile,
        updateProfileState = updateProfileState,
    )
}

@Composable
internal fun ProfileSettingScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBack: () -> Unit,
    uiState: ProfileSettingUiState,
    checkHandleState: CheckHandleState,
    checkHandle: (String) -> Unit,
    resetCheckHandle: () -> Unit,
    signUp: (File, String, String, String) -> Unit,
    signUpState: SignUpState,
    updateProfile: (File, String, String, String) -> Unit,
    updateProfileState: UpdateProfileState
) {
    var backPressed by remember { mutableStateOf(false) }

    BackHandler {
        if (uiState !is ProfileSettingUiState.Error) {
            backPressed = true
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is ProfileSettingUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ProfileSettingUiState.Error -> {
                onBack()
            }

            else -> {
                ProfileSettingContent(
                    modifier = modifier,
                    onBack = onBack,
                    uiState = uiState,
                    checkHandleState = checkHandleState,
                    checkHandle = checkHandle,
                    resetCheckHandle = resetCheckHandle,
                    signUp = signUp,
                    updateProfile = updateProfile,
                )
            }
        }

        when (signUpState) {
            is SignUpState.Loading -> {
                CircularProgressIndicator()
            }

            is SignUpState.Success -> {
                navigateToHome()
            }

            else -> Unit
        }

        when (updateProfileState) {
            is UpdateProfileState.Loading -> {
                CircularProgressIndicator()
            }

            is UpdateProfileState.Success -> {
                onBack()
            }

            else -> Unit
        }

        if (backPressed) {
            PochakAlertDialog(
                onDismiss = { backPressed = false },
                titleText = stringResource(R.string.feature_profile_setting_back_title),
                messageText = stringResource(R.string.feature_profile_setting_back_message),
                cancelButtonText = stringResource(R.string.feature_profile_setting_back_cancel),
                confirmButtonText = stringResource(R.string.feature_profile_setting_back_confirm),
                onConfirmClick = { backPressed = false },
                onCancelClick = {
                    backPressed = false
                    onBack()
                },
            )
        }
    }
}

@Composable
private fun ProfileSettingContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    uiState: ProfileSettingUiState,
    checkHandleState: CheckHandleState,
    checkHandle: (String) -> Unit,
    resetCheckHandle: () -> Unit,
    signUp: (File, String, String, String) -> Unit,
    updateProfile: (File, String, String, String) -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isUpdateProfile = uiState is ProfileSettingUiState.UpdateProfile
    val profileInfo = if (uiState is ProfileSettingUiState.UpdateProfile) {
        uiState.profileInfo
    } else {
        null
    }

    var name by rememberSaveable { mutableStateOf(profileInfo?.name ?: "") }
    var handle by rememberSaveable { mutableStateOf(profileInfo?.handle ?: "") }
    var message by rememberSaveable { mutableStateOf(profileInfo?.message ?: "") }
    var profileImage by rememberSaveable { mutableStateOf(profileInfo?.profileImage) }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { profileImage = it.toString() } ?: run {
                Log.e(TAG, "Profile image uri is null")
            }
        } else if (result.resultCode != Activity.RESULT_CANCELED) {
            Log.e(TAG, "Profile image uri is null")
        }
    }
    val takePhotoIntent =
        Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png", "image/bmp", "image/webp")
            )
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

    // 완료 버튼 활성화 여부
    var actionEnabled by remember { mutableStateOf(false) }

    // 입력값이 모두 채워져 있고, 닉네임 중복 체크가 성공한 경우에만 완료 버튼 활성화
    LaunchedEffect(name, message, profileImage, checkHandleState) {
        actionEnabled = name.isNotEmpty() && message.isNotEmpty() && profileImage != null &&
                (isUpdateProfile || checkHandleState is CheckHandleState.Success)
    }

    // 중복 체크 에러 메시지
    LaunchedEffect(checkHandleState) {
        if (checkHandleState is CheckHandleState.Error) {
            Toast.makeText(context, checkHandleState.message, Toast.LENGTH_SHORT).show()
        }
    }

    // handle 변경 시, 다시 체크
    LaunchedEffect(handle) {
        resetCheckHandle()
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
                IconButton(onClick = {
                    if (actionEnabled) {
                        val file = uriToFile(Uri.parse(profileImage), context)

                        if (isUpdateProfile) {
                            updateProfile(file, name, handle, message)
                        } else {
                            signUp(file, name, handle, message)
                        }
                    }
                }) {
                    Text(
                        text = stringResource(R.string.feature_profile_setting_save),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (actionEnabled) Yellow00 else Gray03,
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = HorizontalPadding)
                // 키보드가 올라와서 입력 필드가 가려지는 경우, 스크롤 가능하도록
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 프로필 이미지
            Image(
                painter =
                if (profileImage == null) painterResource(id = R.drawable.feature_profile_setting_add_profile)
                else rememberAsyncImagePainter(profileImage),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .clickable { takePhotoLauncher.launch(takePhotoIntent) },
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 닉네임
            ProfileInputField(
                title = R.string.feature_profile_setting_name_title,
                hint = R.string.feature_profile_setting_name_hint,
                regexText = R.string.feature_profile_setting_name_regex,
                regex = Regex(".{0,15}$"),
                value = name,
                onValueChange = { name = it },
                isUpdateProfile = isUpdateProfile,
            )

            // 아이디
            ProfileInputField(
                title = R.string.feature_profile_setting_handle_title,
                hint = R.string.feature_profile_setting_handle_hint,
                regexText = R.string.feature_profile_setting_handle_regex,
                regex = Regex("^[a-zA-Z0-9._]{0,15}\$"),
                value = handle,
                onValueChange = { handle = it },
                isUpdateProfile = isUpdateProfile,
                enabled = !isUpdateProfile,
            ) {
                // 아이디 중복 체크 버튼
                Image(
                    painter = painterResource(
                        if (checkHandleState is CheckHandleState.Success) {
                            R.drawable.feature_profile_setting_handle_checked
                        } else {
                            R.drawable.feature_profile_setting_handle_unchecked
                        }
                    ),
                    contentDescription = "Handle Check",
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (checkHandleState is CheckHandleState.UnChecked && handle.isNotEmpty()) {
                                checkHandle(handle)
                            }
                        }
                )
            }

            // 소개
            ProfileInputField(
                title = R.string.feature_profile_setting_message_title,
                hint = R.string.feature_profile_setting_message_hint,
                regexText = R.string.feature_profile_setting_message_regex,
                regex = Regex("^(?:[^\\n]*\\n?){0,2}[^\\n]{0,50}\$"),
                value = message,
                onValueChange = { message = it },
                singleLine = false,
                isUpdateProfile = isUpdateProfile,
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
    isUpdateProfile: Boolean,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    var isValidate by remember { mutableStateOf<Boolean?>(null) }

    // 입력값 오류 체크 후, 0.5초 후에 다시 체크
    LaunchedEffect(isValidate) {
        if (isValidate == false) {
            delay(500L)
            isValidate = value.isNotEmpty()
        }
    }

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
                modifier = Modifier.weight(2f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = {
                            isValidate = (regex.matches(it) && it.firstOrNull() != ' ')
                                .also { isValid ->
                                    if (isValid) {
                                        onValueChange(it)
                                    }
                                }

                            if (it.isEmpty()) {
                                isValidate = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = PochakTextStyle.body2.copy(
                            color = if (enabled) MaterialTheme.colorScheme.onSurface else Gray04,
                        ),
                        singleLine = singleLine,
                        enabled = enabled,
                    )

                    // 입력값이 없을 경우, hint 표시
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(hint),
                            style = PochakTextStyle.body2,
                            color = Gray03,
                        )
                    }
                }

                if (enabled) {
                    content()
                }
            }
        }

        // 입력값이 없거나, 입력값이 유효하지 않을 경우, 오류 메시지 표시
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isUpdateProfile) {
                if (isValidate == true) {
                    Image(
                        painter = painterResource(PochakIcons.Checked),
                        contentDescription = "Check",
                        modifier = Modifier
                            .weight(2f)
                            .padding(end = 4.dp),
                        alignment = Alignment.CenterEnd,
                    )
                } else {
                    Spacer(modifier = Modifier.weight(2f))
                }

                // 입력 포맷 안내문
                Text(
                    modifier = Modifier.weight(8f),
                    text = stringResource(regexText),
                    style = PochakTextStyle.body4,
                    color = when (isValidate) {
                        null -> Gray04
                        true -> PositiveColor
                        false -> ErrorColor
                    }
                )
            }
        }

        HorizontalDivider(color = Gray01)
    }
}
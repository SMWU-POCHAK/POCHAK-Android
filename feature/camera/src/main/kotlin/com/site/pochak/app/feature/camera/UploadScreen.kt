package com.site.pochak.app.feature.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.site.pochak.app.core.data.compressImageFile
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakAlertDialog
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray02
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray0_5
import com.site.pochak.app.core.designsystem.theme.Navy00
import com.site.pochak.app.core.designsystem.theme.Yellow01
import com.site.pochak.app.core.designsystem.theme.Yellow02
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.domain.UploadUiState
import com.site.pochak.app.core.network.model.NetworkMember
import java.io.File

private const val TAG = "UploadScreen"

@Composable
internal fun UploadRoute(
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    val searchMembersUiState by viewModel.searchMembersUiState
    val uploadUiState by viewModel.uploadUiState

    UploadScreen(
        modifier = modifier,
        viewModel = viewModel,
        searchMembersUiState = searchMembersUiState,
        uploadUiState = uploadUiState,
        navigateToHome = navigateToHome,
        onBackClick = onBackClick,
    )
}

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel,
    searchMembersUiState: SearchMembersUiState,
    uploadUiState: UploadUiState,
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val cachedImageFile = File(context.cacheDir, "pochak_image.jpg")
    var capturedImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val caption = rememberSaveable { mutableStateOf("") }
    val handleSearchText = rememberSaveable { mutableStateOf("") }
    val selectedItems = rememberSaveable { mutableStateOf(emptyList<String>()) }
    var backPressed by remember { mutableStateOf(false) }

    LaunchedEffect(uploadUiState) {
        if (uploadUiState is UploadUiState.Success) {
            navigateToHome()
        }
    }

    BackHandler {
        backPressed = true
    }

    if (cachedImageFile.exists()) {
        capturedImageBitmap = BitmapFactory.decodeFile(cachedImageFile.absolutePath)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PochakTopAppBar(
                leftContent = { BackButton(onClick = { backPressed = true }) },
                centerContent = { Text(text = stringResource(R.string.feature_camera_upload)) },
                rightContent = {
                    IconButton(
                        onClick = {
                            viewModel.postPost(
                                postImage = compressImageFile(cachedImageFile, context),
                                taggedMemberHandleList = selectedItems.value,
                                caption = caption.value
                            )
                        },
                        enabled = selectedItems.value.isNotEmpty() // selectedItems가 비어 있으면 비활성화
                    ) {
                        Text(
                            text = stringResource(R.string.feature_camera_upload_button),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedItems.value.isNotEmpty()) Yellow01 else Gray03, // 상태에 따른 색상
                        )
                    }
                },
            )

            if (capturedImageBitmap != null) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = HorizontalPadding)
                ) {
                    CapturedImageAndCaptionField(
                        modifier = modifier,
                        capturedImageBitmap = capturedImageBitmap!!,
                        caption = caption
                    )

                    HorizontalDivider(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        color = Gray01
                    )

                    SearchScreen(
                        modifier = modifier,
                        viewModel = viewModel,
                        searchMembersUiState = searchMembersUiState,
                        handleSearchText = handleSearchText,
                        selectedItems = selectedItems
                    )
                }
            }
            if (backPressed) {
                PochakAlertDialog(
                    onDismiss = { backPressed = false },
                    titleText = stringResource(R.string.feature_camera_back_title),
                    messageText = stringResource(R.string.feature_camera_back_message),
                    cancelButtonText = stringResource(R.string.feature_camera_back_cancel),
                    confirmButtonText = stringResource(R.string.feature_camera_back_confirm),
                    onConfirmClick = { backPressed = false },
                    onCancelClick = {
                        backPressed = false
                        onBackClick()
                    },
                )
            }
        }

        // 업로드 중일 때 로딩 표시
        if (uploadUiState is UploadUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (uploadUiState is UploadUiState.Failed) {
            PochakAlertDialog(
                onDismiss = {},
                titleText = stringResource(R.string.feature_camera_upload_fail_title),
                messageText = stringResource(R.string.feature_camera_upload_fail_message),
                confirmButtonText = stringResource(R.string.feature_camera_dialog_confirm),
            )
        }
    }
}

@Composable
private fun CapturedImageAndCaptionField(
    modifier: Modifier = Modifier,
    capturedImageBitmap: Bitmap,
    caption: MutableState<String>
) {
    val maxChars = 50

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            bitmap = capturedImageBitmap.asImageBitmap(),
            contentDescription = "Captured Image",
            modifier = modifier
                .size(111.dp, 148.dp)
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 25.dp)
        ) {
            BasicTextField(
                value = caption.value,
                onValueChange = { caption.value = it.take(maxChars) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(128.dp),
                decorationBox = { innerTextField ->
                    if (caption.value.isEmpty()) {
                        Text(
                            modifier = modifier
                                .align(Alignment.Start),
                            text = stringResource(id = R.string.feature_camera_input_caption),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge

                        )
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = "${caption.value.length}/$maxChars",
                modifier = modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel,
    searchMembersUiState: SearchMembersUiState,
    handleSearchText: MutableState<String>,
    selectedItems: MutableState<List<String>>,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) } // 포커스 여부
    val isSearching = handleSearchText.value.isNotEmpty() || isFocused // 포커스 여부와 입력 상태 확인
    val searchResults by viewModel.searchResults
    val listState = rememberLazyListState() // LazyColumn의 스크롤 상태를 저장
    var showTagDialog by remember { mutableStateOf(false) }  // State to control dialog visibility

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.feature_camera_tag),
            modifier = modifier
                .align(Alignment.TopStart),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Start
        )

        // FlowRow - 선택된 아이템들
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 88.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
        ) {
            selectedItems.value.forEach { item ->
                SelectedItemView(
                    modifier = modifier,
                    item,
                    onDeleteClick = {
                        selectedItems.value -= it
                    }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Top, // 취소 버튼을 Row의 위쪽에 정렬
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Gray0_5, shape = RoundedCornerShape(18.dp))
                ) {
                    Icon(
                        painter = painterResource(id = PochakIcons.Search),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(24.dp)
                    )

                    BasicTextField(
                        value = handleSearchText.value,
                        onValueChange = { newValue ->
                            handleSearchText.value = newValue
                            if (newValue.isEmpty()) {
                                viewModel.clearSearchResults() // 검색어가 비어 있으면 결과 초기화
                            } else {
                                viewModel.searchMembers(newValue) // 검색어 변경 시 API 호출
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused // 포커스 상태 업데이트
                            },
                        decorationBox = { innerTextField ->
                            if (handleSearchText.value.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.feature_camera_tag_friend),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true, // 한 줄 입력만 허용
                        maxLines = 1 // 확실히 한 줄로 제한
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 검색 결과 LazyColumn
                when (searchMembersUiState) {
                    is SearchMembersUiState.Success -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Gray02, shape = RoundedCornerShape(18.dp))
                                .heightIn(max = 250.dp)
                                .clipToBounds()
                        ) {
                            items(searchResults.size) { index ->
                                val member = searchResults[index]
                                SearchResultItem(
                                    result = member,
                                    onResultClick = {
                                        handleSearchText.value = ""

                                        if (member.handle !in selectedItems.value && selectedItems.value.size < 5) {
                                            selectedItems.value += member.handle
                                        } else if (selectedItems.value.size >= 5) {
                                            showTagDialog = true
                                        }
                                        viewModel.clearSearchResults()  // 검색 결과 초기화
                                        focusManager.clearFocus()
                                    },
                                    showDivider = index < searchResults.lastIndex
                                )
                            }
                        }

                        if (showTagDialog) {
                            PochakAlertDialog(
                                onDismiss = { showTagDialog = false },
                                titleText = stringResource(R.string.feature_camera_tag_dialog_title),
                                confirmButtonText = stringResource(R.string.feature_camera_dialog_confirm),
                                onConfirmClick = { showTagDialog = false },
                            )
                        }
                    }

                    is SearchMembersUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp) // LazyColumn과 동일한 높이 설정
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    else -> {
                    }
                }
            }

            // 취소 버튼
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .clickable(
                            onClick = {
                                handleSearchText.value = ""
                                viewModel.clearSearchResults()
                                focusManager.clearFocus()
                                isFocused = false
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .wrapContentSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_camera_tag_cancel),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    result: NetworkMember,
    onResultClick: (String) -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = modifier
            .padding(horizontal = HorizontalPadding)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onResultClick(result.handle) }
                .padding(vertical = 12.dp)
        ) {
            AsyncImage(
                modifier = modifier
                    .size(40.dp)
                    .clip(CircleShape),
                model = result.profileImage,
                contentDescription = "profile image",
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = modifier
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = result.handle, style = MaterialTheme.typography.bodySmall, color = Color.Black
                )
                Text(
                    modifier = modifier
                        .padding(top = 2.dp),
                    text = result.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black,
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Gray03
            )
        }
    }
}

@Composable
fun SelectedItemView(
    modifier: Modifier = Modifier,
    item: String,
    onDeleteClick: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Yellow02, shape = RoundedCornerShape(18.dp))
            .wrapContentWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = item, color = Navy00, style = MaterialTheme.typography.bodySmall)

        Image(
            modifier = modifier
                .padding(start = 4.dp)
                .size(20.dp)
                .clickable {
                    onDeleteClick(item)
                },
            painter = painterResource(id = PochakIcons.Delete),
            contentDescription = "profile image"
        )
    }
}
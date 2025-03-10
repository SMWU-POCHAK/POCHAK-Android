package com.site.pochak.app.feature.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.Yellow01
import com.site.pochak.app.core.model.data.Alarm
import com.site.pochak.app.core.model.data.AlarmType
import com.site.pochak.app.core.model.data.getDescription
import com.site.pochak.app.core.model.data.getElapsedTime
import com.site.pochak.app.core.model.data.getPostImage
import com.site.pochak.app.core.model.data.getProfileImageUrl

@Composable
internal fun AlarmRoute(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = hiltViewModel(),
    navigateToPostDetail: (Int) -> Unit,
    navigateToProfile: (String) -> Unit
) {
    val allAlarms = viewModel.allAlarms.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    AlarmScreen(
        modifier = modifier,
        viewModel = viewModel,
        allAlarms = allAlarms.value,
        isLoading = isLoading.value,
        isRefreshing = isRefreshing.value,
        onLoadPage = viewModel::loadPage,
        onPostAlarmClick = navigateToPostDetail,
        onFollowAlarmClick = navigateToProfile
    )
}

@Composable
internal fun AlarmScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel,
    allAlarms: List<Alarm>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
    onPostAlarmClick: (Int) -> Unit,
    onFollowAlarmClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PochakTopAppBar(
            centerContent = { Text(text = stringResource(R.string.feature_alarm_title))},
        )
        AlarmContent(
            modifier = modifier,
            viewModel = viewModel,
            allAlarms = allAlarms,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
            onPostDetailClick = onPostAlarmClick,
            onFollowAlarmClick = onFollowAlarmClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmContent(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel,
    allAlarms: List<Alarm>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
    onPostDetailClick: (Int) -> Unit,
    onFollowAlarmClick: (String) -> Unit
) {
    val selectedTagId by viewModel.selectedTagId.collectAsState()
    val postPreviewUiState by viewModel.postPreviewUiState
    var showBottomSheet : Boolean by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedTagId) {
        if (selectedTagId != null) {
            showBottomSheet = true
        }
    }

    RefreshableLazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        refreshEnabled = true,
        isRefreshing = isRefreshing,
        columns = GridCells.Fixed(1),
        loadMore = onLoadPage,
    ) {
        items(allAlarms) { alarm ->
            AlarmItem(
                modifier = Modifier,
                alarm = alarm,
                viewModel = viewModel,
                onPostDetailClick = onPostDetailClick,
                onFollowAlarmClick = onFollowAlarmClick
            )
        }

        if (isLoading && !isRefreshing) {
            item { }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (postPreviewUiState is PostPreviewUiState.Success) {
        val postPreview = (postPreviewUiState as PostPreviewUiState.Success).postPreview
        TagApprovalBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismiss = {
                showBottomSheet = false
                viewModel.clearSelectedTagAlarm()
                viewModel.clearApproveTagUiState()
            },
            postPreviewDetail = postPreview,
            viewModel = viewModel,
            tagId = selectedTagId!!.toInt()
        )
    }
}

@Composable
fun AlarmItem(
    modifier: Modifier,
    alarm: Alarm,
    viewModel: AlarmViewModel,
    onPostDetailClick: (Int) -> Unit,
    onFollowAlarmClick: (String) -> Unit
) {
    var isClicked by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(
                if (alarm.alarmType == AlarmType.TAG_APPROVAL) {
                    Yellow01
                } else if (isClicked || alarm.isChecked) {
                    MaterialTheme.colorScheme.surface
                } else {
                    Yellow01
                }
            )
            .padding(horizontal = 20.dp)
            .height(76.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Divider(
            color = Gray01,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isClicked = true
                    viewModel.checkAlarm(alarm.alarmId.toInt()) // 알람 상태 확인

                    when (alarm.alarmType) {
                        AlarmType.TAG_APPROVAL -> {
                            viewModel.selectTagAlarm(alarm.tagId) // 현재 알람 선택
                            viewModel.getPostPreview(alarm.alarmId) // 태그 미리보기 가져오기
                        }
                        AlarmType.FOLLOW -> {
                            alarm.memberHandle?.let { onFollowAlarmClick(it) } // 팔로우한 유저 프로필로 이동
                        }
                        else -> {
                            onPostDetailClick(alarm.postId!!.toInt()) // 게시물 상세로 이동
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(alarm.alarmType) {
                AlarmType.MOMENT_POST -> {
                    Box(
                        modifier = Modifier.size(62.dp,44.dp)
                    ) {
                        AsyncImage(
                            model = alarm.ownerProfileImage,
                            contentDescription = "first user profile image",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        AsyncImage(
                            model = alarm.memberProfileImage,
                            contentDescription = "second user profile image",
                            modifier = Modifier
                                .size(44.dp)
                                .offset(x = 22.dp) // 가로로 겹치도록 이동
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                else -> {
                    AsyncImage(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 6.dp)
                            .size(44.dp)
                            .clip(CircleShape),
                        model = alarm.getProfileImageUrl(),
                        contentDescription = "alarm profile image",
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = alarm.getDescription(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = alarm.getElapsedTime(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray04
                )
            }

            Spacer(modifier = Modifier.width(25.dp))

            Box(
                modifier = Modifier.width(35.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(35.dp, 46.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    model = alarm.getPostImage(),
                    contentDescription = "post image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
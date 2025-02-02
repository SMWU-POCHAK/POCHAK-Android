package com.site.pochak.app.feature.alarm

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
import com.site.pochak.app.core.model.data.Alarm
import com.site.pochak.app.core.model.data.getDescription
import com.site.pochak.app.core.model.data.getElapsedTime
import com.site.pochak.app.core.model.data.getPostImage
import com.site.pochak.app.core.model.data.getProfileImageUrl

@Composable
internal fun AlarmRoute(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = hiltViewModel(),
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
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PochakTopAppBar(
            centerContent = { Text(text = stringResource(R.string.feature_alarm_title)) },
        )
        AlarmContent(
            modifier = modifier,
            viewModel = viewModel,
            allAlarms = allAlarms,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
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
) {
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
                viewModel = viewModel
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
}

@Composable
fun AlarmItem(
    modifier: Modifier,
    alarm: Alarm,
    viewModel: AlarmViewModel
) {
    Column(
        modifier = modifier
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
                .clickable { /* 클릭 동작 추가 */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(44.dp)
                    .clip(CircleShape),
                model = alarm.getProfileImageUrl(),
                contentDescription = "alarm image",
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier
                    .weight(1f),
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

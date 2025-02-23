package com.site.pochak.app.feature.alarm

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.RoundedButton
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.network.model.NetworkPostPreview
import kotlinx.coroutines.launch

private const val TAG = "TagApprovalBottomSheet"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagApprovalBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    postPreviewDetail: NetworkPostPreview,
    viewModel: AlarmViewModel,
    tagId: Int
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val approveTagUiState by viewModel.approveTagUiState

    fun hideSheet() {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
    }

    LaunchedEffect(approveTagUiState) {
        if (approveTagUiState is AlarmUiState.Success) {
            Log.d(TAG, "Tag approval successful. Hiding sheet.")
            viewModel.loadPage(true)
            hideSheet() // 성공 시 바텀시트 닫기
        } else if (approveTagUiState is AlarmUiState.Error) {
            Log.e(TAG, "Tag approval failed.")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { hideSheet() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPadding, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircleCropAsyncImage(
                            modifier = Modifier.size(50.dp),
                            imageUrl = postPreviewDetail.ownerProfileImage,
                            contentDescription = "profile image",
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val maxTagCount = 2
                            Text(
                                text = postPreviewDetail.tagList.map { it.handle }.take(maxTagCount)
                                    .joinToString(" · ") { it + "님" } + if (postPreviewDetail.tagList.size > maxTagCount) "..." else "",
                                style = PochakTextStyle.body1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = postPreviewDetail.ownerHandle + "님이 포착",
                                style = PochakTextStyle.body4,
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RoundedButton(
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            text = stringResource(R.string.feature_alarm_tag_accept),
                            onClick = { viewModel.postApproveTag(tagId, true) },
                        )

                        RoundedButton(
                            backgroundColor = Gray03,
                            text = stringResource(R.string.feature_alarm_tag_reject),
                            onClick = { viewModel.postApproveTag(tagId, false) },
                        )
                    }
                }

                AsyncImage(
                    model = postPreviewDetail.postImage,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

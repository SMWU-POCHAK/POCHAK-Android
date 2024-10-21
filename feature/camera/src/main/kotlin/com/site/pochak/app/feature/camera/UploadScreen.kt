package com.site.pochak.app.feature.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.accompanist.flowlayout.FlowRow
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray02
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray0_5
import com.site.pochak.app.core.designsystem.theme.Navy00
import com.site.pochak.app.core.designsystem.theme.Yellow02
import java.io.File

@Composable
internal fun UploadRoute(
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    UploadScreen(
        modifier = modifier,
    )
}

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cachedImageFile = File(context.cacheDir, "pochak_image.jpg")
    var capturedImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    if (cachedImageFile.exists()) {
        capturedImageBitmap = BitmapFactory.decodeFile(cachedImageFile.absolutePath)
    }

    if (capturedImageBitmap != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
            ) {
                CapturedImageAndCaptionField(
                    modifier = modifier,
                    capturedImageBitmap = capturedImageBitmap!!,
                )

                HorizontalDivider(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    color = Gray01
                )

                SearchScreen(
                    modifier = modifier,
                )
            }
    }
}

@Composable
private fun CapturedImageAndCaptionField(
    modifier: Modifier = Modifier,
    capturedImageBitmap: Bitmap,
) {
    var caption by rememberSaveable { mutableStateOf("") }
    val maxChars = 50

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
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
                value = caption,
                onValueChange = { caption = it.take(maxChars) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(128.dp),
                decorationBox = { innerTextField ->
                    if (caption.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.feature_camera_input_caption),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = modifier
                                .align(Alignment.Start)
                        )
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = "${caption.length}/$maxChars",
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
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var handleSearchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<String>()) }
    var selectedItems by remember { mutableStateOf(emptyList<String>()) }

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

        // 검색 Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.TopCenter)
                .offset(y = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Gray0_5)
        ) {
            Icon(
                painter = painterResource(id = PochakIcons.Search),
                contentDescription = "Search Icon",
                modifier = modifier
                    .size(24.dp)
                    .offset(x = 12.dp)
            )

            BasicTextField(
                value = handleSearchText,
                onValueChange = { handleSearchText = it },
                modifier = modifier
                    .weight(1f)
                    .offset(x = 20.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            // 포커스되면 searchResults를 업데이트
                            searchResults = listOf(
                                "태그1", "태그22222222", "태그3", "태그4",
                                "태그5", "태그2", "태그3", "태그4", "태그5"
                            )
                        }
                    },
                decorationBox = { innerTextField ->
                    if (handleSearchText.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.feature_camera_tag_friend),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        // FlowRow - 선택된 아이템들
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 88.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
        ) {
            selectedItems.forEach { item ->
                SelectedItemView(
                    modifier = modifier,
                    item,
                    onDeleteClick = {
                        selectedItems = selectedItems - it
                    }
                )
            }
        }

        // LazyColumn - 검색 결과 리스트
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 84.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Gray02)
                .heightIn(max = 250.dp)
        ) {
            itemsIndexed(searchResults) { index, result ->
                SearchResultItem(
                    modifier = Modifier,
                    result = result,
                    onResultClick = {
                        // 중복 체크 및 5개 제한
                        if (result !in selectedItems && selectedItems.size < 5) {
                            selectedItems = selectedItems + result
                        }
                        searchResults = emptyList()
                        focusManager.clearFocus()
                    },
                    showDivider = index < searchResults.lastIndex
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    result: String,
    onResultClick: (String) -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onResultClick(result) }
                .padding(vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(id = PochakIcons.Profile),
                contentDescription = "profile image",
                modifier = modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = modifier
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = result, style = MaterialTheme.typography.bodySmall, color = Color.Black
                )
                Text(
                    text = result,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black,
                    modifier = modifier
                        .padding(top = 2.dp)
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
            .background(Yellow02, shape = RoundedCornerShape(6.dp))
            .wrapContentWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = item, color = Navy00, style = MaterialTheme.typography.bodySmall)

        Image(
            painter = painterResource(id = PochakIcons.Delete),
            contentDescription = "profile image",
            modifier = modifier
                .padding(start = 4.dp)
                .size(20.dp)
                .clickable {
                    onDeleteClick(item)
                },
        )
    }
}
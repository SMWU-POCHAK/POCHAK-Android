package com.site.pochak.app.feature.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray03
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.designsystem.theme.Gray0_5
import com.site.pochak.app.core.domain.SearchMembersUiState
import com.site.pochak.app.core.model.data.RecentSearch
import com.site.pochak.app.core.network.model.NetworkMember

@Composable
internal fun SearchHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel = hiltViewModel(),
) {
    val searchMembersUiState by viewModel.searchMembersUiState.collectAsStateWithLifecycle()

    SearchHistoryScreen(
        modifier = modifier.padding(horizontal = HorizontalPadding),
        viewModel = viewModel,
        searchMembersUiState = searchMembersUiState
    )
}

@Composable
internal fun SearchHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel,
    searchMembersUiState: SearchMembersUiState,
) {
    val recentSearches by viewModel.recentSearches.observeAsState(emptyList())
    val isSearching = searchMembersUiState is SearchMembersUiState.Success ||
            searchMembersUiState is SearchMembersUiState.Loading

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Search bar at the top
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
        )

        // Show Recent Searches or Search Results
        if (!isSearching) {
            // When not searching, show RecentSearchesContent
            RecentSearchesContent(
                recentSearches = recentSearches,
                onDelete = { viewModel.removeSearchItem(it.id) },
                onClearAll = { viewModel.clearAllSearches() }
            )
        } else {
            // When searching, show SearchResultsContent
            SearchResultsContent(
                modifier = Modifier.fillMaxWidth(),
                searchMembersUiState = searchMembersUiState,
                viewModel = viewModel,
                onClick = {
                    // 해당 프로필로 이동
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel,
) {
    val handleSearchText = rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) } // 포커스 여부
    val isSearching = handleSearchText.value.isNotEmpty() || isFocused // 포커스 여부와 입력 상태 확인

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top, // 취소 버튼을 Row의 위쪽에 정렬
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp)
                    .height(48.dp)
                    .background(Gray0_5, shape = RoundedCornerShape(18.dp))
            ) {
                Icon(
                    painter = painterResource(id = PochakIcons.Search),
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .padding(start = 10.dp)
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
                        .padding(horizontal = 12.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused // 포커스 상태 업데이트
                        },
                    decorationBox = { innerTextField ->
                        if (handleSearchText.value.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.feature_post_search_placeholder),
                                color = Gray03,
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
                        text = stringResource(id = R.string.feature_post_search_cancel),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun RecentSearchesContent(
    recentSearches: List<RecentSearch>,
    onDelete: (RecentSearch) -> Unit,
    onClearAll: () -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.feature_post_recent_search),
                style = MaterialTheme.typography.bodyLarge,
                color = Gray04
            )
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = onClearAll,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .wrapContentSize()
            ) {
                Text(
                    text = stringResource(id = R.string.feature_post_recent_search_clear_all),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray04,
                )
            }
        }

        // 최근 검색어 리스트
        RecentSearchItem(
            modifier = Modifier,
            recentSearches = recentSearches,
            onDelete = onDelete,
            onClick = {
                // 해당 프로필로 이동
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentSearchItem(
    modifier: Modifier,
    recentSearches: List<RecentSearch>,
    onDelete: (RecentSearch) -> Unit,
    onClick: (RecentSearch) -> Unit
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 0.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (recentSearches.isNotEmpty()) {
            items(recentSearches) { recentSearch ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(recentSearch) // 추가 클릭 이벤트 핸들링
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        model = recentSearch.profileImage,
                        contentDescription = "profile image",
                        contentScale = ContentScale.Crop,
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 사용자 이름 및 추가 정보
                    Column {
                        Text(
                            text = recentSearch.handle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = recentSearch.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsContent(
    modifier: Modifier,
    searchMembersUiState: SearchMembersUiState,
    viewModel: SearchHistoryViewModel,
    onClick: (NetworkMember) -> Unit // 클릭 시 실행할 콜백 함수
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 0.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        loadMore = { viewModel.searchMembers("keyword") },
    ) {
        when (searchMembersUiState) {
            is SearchMembersUiState.Success -> {
                val members = searchMembersUiState.members
                items(members) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.addSearchItem(
                                    handle = member.handle,
                                    name = member.name,
                                    profileImageUrl = member.profileImage
                                )
                                onClick(member) // 추가 클릭 이벤트 핸들링
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape),
                            model = member.profileImage,
                            contentDescription = "profile image",
                            contentScale = ContentScale.Crop,
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // 사용자 이름 및 추가 정보
                        Column {
                            Text(
                                text = member.handle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            is SearchMembersUiState.Error -> {
                // dialog
            }
            else -> { }
        }
    }
}
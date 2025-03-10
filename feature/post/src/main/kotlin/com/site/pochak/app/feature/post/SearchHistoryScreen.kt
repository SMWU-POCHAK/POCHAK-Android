package com.site.pochak.app.feature.post

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
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
import com.site.pochak.app.core.model.data.RecentSearch
import com.site.pochak.app.core.network.model.NetworkMember

@Composable
internal fun SearchHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel = hiltViewModel(),
    navigateToProfile: (String) -> Unit
) {
    SearchHistoryScreen(
        modifier = modifier.padding(horizontal = HorizontalPadding),
        viewModel = viewModel,
        navigateToProfile = navigateToProfile
    )
}

@Composable
internal fun SearchHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel,
    navigateToProfile: (String) -> Unit
) {
    val recentSearches by viewModel.recentSearches.observeAsState(emptyList())
    val currentKeyword by viewModel.currentKeyword.collectAsStateWithLifecycle()
    val isSearching = currentKeyword.isNotEmpty()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        SearchBar(
            modifier = Modifier,
            viewModel = viewModel,
        )

        if (isSearching) {
            SearchResultsContent(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel,
                searchResults = searchResults,
                isLoading = isLoading,
                onClick = {
                    navigateToProfile(it.handle)
                }
            )
        } else {
            RecentSearchesContent(
                modifier = Modifier,
                viewModel = viewModel,
                recentSearches = recentSearches,
                onClick = {
                    navigateToProfile(it.handle)
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
    val searchText = rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val isSearching = searchText.value.isNotEmpty() || isFocused

    if (isSearching) {
        BackHandler {
            clearSearchBar(searchText, viewModel, focusManager) { isFocused = false }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            SearchInputField(
                modifier = Modifier.weight(1f),
                searchText = searchText,
                focusRequester = focusRequester,
                onFocusChanged = { isFocused = it },
                onValueChanged = {
                    searchText.value = it
                    viewModel.updateKeyword(it)
                    if (it.isEmpty()) viewModel.clearSearchResults() else viewModel.searchMembers(isRefresh = true)
                }
            )
            if (isSearching) CancelButton { clearSearchBar(searchText, viewModel, focusManager) { isFocused = false } }
        }
    }
}

@Composable
fun SearchInputField(
    modifier: Modifier,
    searchText: MutableState<String>,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onValueChanged: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(Gray0_5, shape = RoundedCornerShape(18.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = PochakIcons.Search),
                contentDescription = "Search Icon",
                modifier = Modifier.padding(start = 12.dp).size(24.dp)
            )

            BasicTextField(
                value = searchText.value,
                onValueChange = onValueChanged,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChanged(it.isFocused) },
                decorationBox = { innerTextField ->
                    if (searchText.value.isEmpty()) {
                        PlaceholderText()
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlaceholderText() {
    Text(
        text = stringResource(id = R.string.feature_post_search_placeholder),
        color = Gray03,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun CancelButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable(
                onClick = onClick,
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

@Composable
fun RecentSearchesContent(
    modifier: Modifier,
    viewModel: SearchHistoryViewModel,
    recentSearches: List<RecentSearch>,
    onClick: (RecentSearch) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        HeaderWithClearButton(
            modifier = Modifier,
            title = stringResource(id = R.string.feature_post_recent_search),
            onClearClick = { viewModel.clearAllSearches() }
        )

        RecentSearchItemList(
            modifier = Modifier,
            viewModel = viewModel,
            recentSearches = recentSearches,
            onClick = { onClick(it) }
        )
    }
}

@Composable
fun HeaderWithClearButton(
    modifier: Modifier,
    title: String,
    onClearClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Gray04
        )
        Text(
            text = stringResource(id = R.string.feature_post_recent_search_clear_all),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray04,
            modifier = Modifier.clickable(
                onClick = onClearClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ).wrapContentSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentSearchItemList(
    modifier: Modifier,
    viewModel: SearchHistoryViewModel,
    recentSearches: List<RecentSearch>,
    onClick: (RecentSearch) -> Unit
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Fixed(1),
    ) {
        items(recentSearches) { member ->
            RecentSearchRow(
                modifier = Modifier,
                member = member,
                viewModel = viewModel,
                onClick = onClick)
        }
    }
}

@Composable
fun RecentSearchRow(
    modifier: Modifier,
    member: RecentSearch,
    viewModel: SearchHistoryViewModel,
    onClick: (RecentSearch) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.addSearchItem(
                    handle = member.handle,
                    name = member.name,
                    profileImageUrl = member.profileImage
                )
                onClick(member)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(52.dp).clip(CircleShape),
            model = member.profileImage,
            contentDescription = "profile image",
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = member.handle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = PochakIcons.DeleteGray04),
            contentDescription = "delete icon",
            modifier = Modifier.size(20.dp).clickable { viewModel.removeSearchItem(member.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsContent(
    modifier: Modifier = Modifier,
    viewModel: SearchHistoryViewModel,
    searchResults: List<NetworkMember>,
    isLoading: Boolean,
    onClick: (NetworkMember) -> Unit
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        loadMore = { isRefresh -> viewModel.searchMembers(isRefresh) },
    ) {
        items(searchResults) { member ->
            SearchResultRow(
                modifier = Modifier,
                member = member,
                viewModel = viewModel,
                onClick = onClick)
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun SearchResultRow(
    modifier: Modifier,
    member: NetworkMember,
    viewModel: SearchHistoryViewModel,
    onClick: (NetworkMember) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.addSearchItem(
                    handle = member.handle,
                    name = member.name,
                    profileImageUrl = member.profileImage
                )
                onClick(member)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(52.dp).clip(CircleShape),
            model = member.profileImage,
            contentDescription = "profile image",
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = member.handle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

/**
 * 검색창 초기화 함수.
 *
 * @param searchText 현재 검색창에 입력된 텍스트 상태.
 * @param viewModel 검색 기록을 관리하는 ViewModel.
 * @param focusManager 포커스를 관리하는 FocusManager.
 * @param onClearFocus 포커스를 해제할 때 실행할 콜백 함수.
 */
fun clearSearchBar(
    searchText: MutableState<String>,
    viewModel: SearchHistoryViewModel,
    focusManager: FocusManager,
    onClearFocus: () -> Unit
) {
    searchText.value = ""
    viewModel.clearSearchResults()
    viewModel.updateKeyword("")
    focusManager.clearFocus()
    onClearFocus()
}
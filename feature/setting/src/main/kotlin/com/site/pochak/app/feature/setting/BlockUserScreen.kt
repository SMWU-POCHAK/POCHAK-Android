package com.site.pochak.app.feature.setting

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.component.RoundedButton
import com.site.pochak.app.core.designsystem.theme.Gray01
import com.site.pochak.app.core.designsystem.theme.Gray04
import com.site.pochak.app.core.model.data.Member

@Composable
fun BlockUserRoute(
    modifier: Modifier = Modifier,
    viewModel: BlockUserViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val blockMembers = viewModel.blockedMembers.collectAsLazyPagingItems()

    BlockUserScreen(
        modifier = modifier,
        onBack = onBack,
        blockMembers = blockMembers,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlockUserScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    blockMembers: LazyPagingItems<Member>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            leftContent = { BackButton(onClick = onBack) },
            centerContent = {
                Text(
                    text = "차단관리",
                    style = PochakTextStyle.body0,
                )
            },
        )

        RefreshableLazyVerticalGrid(
            refreshEnabled = false,
            columns = GridCells.Fixed(1),
        ) {
            items(blockMembers.itemCount) { index ->
                val member = blockMembers[index] ?: return@items

                MemberItem(
                    member = member,
                    onBlock = {},
                )
            }
        }
    }
}

@Composable
private fun MemberItem(
    member: Member,
    onBlock: () -> Unit
) {
    var isFollow by remember { mutableStateOf(true) }

    Column {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleCropAsyncImage(
                modifier = Modifier.size(52.dp),
                imageUrl = member.profileImage,
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = member.handle,
                    style = PochakTextStyle.body3_1,
                )

                Text(
                    text = member.name,
                    style = PochakTextStyle.body3,
                )
            }

            RoundedButton(
                text = "차단해제",
                backgroundColor = Gray04,
                onClick = onBlock,
            )
        }

        HorizontalDivider(color = Gray01)
    }
}
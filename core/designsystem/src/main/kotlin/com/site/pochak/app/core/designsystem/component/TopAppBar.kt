package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.site.pochak.app.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PochakTopAppBar(
    title: @Composable () -> Unit,
    showNavigationIcon: Boolean = false,
    showActionIcon: Boolean = false,
    modifier: Modifier = Modifier,
    actionIcon: @Composable () -> Unit = {},
    onNavigationClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = { title() },
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_left_arrow),
                        contentDescription = "Back",
                    )
                }
            }
        },
        actions = {
            if (showActionIcon) {
                actionIcon()
            }
        }
    )
}
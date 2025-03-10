package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PochakTopAppBar(
    leftContent: @Composable () -> Unit = {},
    centerContent: @Composable () -> Unit = {},
    rightContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        navigationIcon = { leftContent() },
        title = { centerContent() },
        actions = { rightContent() }
    )
}

@Composable
fun PochakNavigationTopAppBar(
    onBack: () -> Unit,
    title: String = "",
    rightContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    PochakTopAppBar(
        leftContent = { BackButton(onClick = onBack) },
        centerContent = {
            Text(
                text = title,
                style = PochakTextStyle.body0,
            )
        },
        rightContent = rightContent,
    )
}
package com.site.pochak.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PochakNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.shadow(elevation = 20.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .defaultMinSize(minHeight = NavigationBarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(NavigationBarItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

private val NavigationBarHeight: Dp = 80.0.dp
private val NavigationBarItemHorizontalPadding: Dp = 8.dp
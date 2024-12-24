package com.site.pochak.app.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.site.pochak.app.core.designsystem.R
import com.site.pochak.app.core.designsystem.icon.PochakIcons

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_left_arrow),
            contentDescription = null,
        )
    }
}

@Composable
fun MoreButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(PochakIcons.More),
            contentDescription = null,
        )
    }
}
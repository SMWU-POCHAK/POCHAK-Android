package com.site.pochak.app.feature.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun PostRoute(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel(),
) {
    PostScreen(
        modifier = modifier,
    )
}

@Composable
internal fun PostScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Post Screen")
        }
    }
}
package com.site.pochak.app.feature.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun AlarmRoute(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    AlarmScreen(
        modifier = modifier,
    )
}

@Composable
internal fun AlarmScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Alarm Screen")
        }
    }
}
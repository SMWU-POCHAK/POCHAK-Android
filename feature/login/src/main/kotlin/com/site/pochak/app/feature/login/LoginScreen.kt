package com.site.pochak.app.feature.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
) {
    LoginScreen(
        modifier = modifier,
        onLoginSuccess = onLoginSuccess,
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Login Screen")

            Button(
                onClick = onLoginSuccess,
            ) {
                Text(text = "Login")
            }
        }
    }
}
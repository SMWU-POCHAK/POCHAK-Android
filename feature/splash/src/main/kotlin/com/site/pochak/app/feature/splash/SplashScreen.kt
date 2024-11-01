package com.site.pochak.app.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.domain.GoogleLoginState

private const val TAG = "SplashScreen"

@Composable
internal fun SplashRoute(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val splashUiState by viewModel.googleLoginState.collectAsStateWithLifecycle()

    SplashScreen(
        modifier = modifier,
        navigateToLogin = navigateToLogin,
        navigateToHome = navigateToHome,
        splashUiState = splashUiState,
    )
}

@Composable
internal fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    splashUiState: SplashUiState,
) {
    LaunchedEffect(splashUiState) {
        when (splashUiState) {
            SplashUiState.Loading -> Unit
            SplashUiState.LoginSuccess -> navigateToHome()
            SplashUiState.LoginFailed -> navigateToLogin()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.feature_splash_logo),
            contentDescription = "Splash Screen Logo",
            modifier = Modifier
                .width(244.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()

            Text(text = stringResource(id = R.string.feature_splash_loading_text))
        }
    }
}

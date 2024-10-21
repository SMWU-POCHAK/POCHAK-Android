package com.site.pochak.app.feature.login

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private val TAG = "LoginScreen"

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToSignUp: (String) -> Unit,
) {
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        navigateToHome = navigateToHome,
        navigateToSignUp = navigateToSignUp,
        loginUiState = loginUiState,
        onGoogleLogin = viewModel::googleLogin,
        resetLoginUiState = viewModel::resetLoginUiState
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    navigateToSignUp: (String) -> Unit,
    loginUiState: LoginUiState,
    onGoogleLogin: (String) -> Unit,
    resetLoginUiState: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberGoogleLoginLauncher(context, coroutineScope, onGoogleLogin)
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val client = GoogleSignIn.getClient(context, options)

    LaunchedEffect(loginUiState) {
        navigateToSignUp(
            Json.encodeToString(
                NetworkLoginInfo.serializer(),
                NetworkLoginInfo(
                    id = -1,
                    socialId = "",
                    name = "",
                    email = "",
                    handle = "",
                    socialType = "",
                    accessToken = "",
                    refreshToken = "",
                    isNewMember = true
                )
            )
        )

        when (loginUiState) {
            LoginUiState.Success -> navigateToHome()
            is LoginUiState.Error -> Log.e(TAG, "Login Error: ${loginUiState.message}")
            is LoginUiState.SignUp -> {
                navigateToSignUp(
                    Json.encodeToString(
                        NetworkLoginInfo.serializer(),
                        loginUiState.loginInfo
                    )
                )
                resetLoginUiState()
            }

            else -> Unit
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LoginLogo(modifier = Modifier.align(Alignment.Center))

        LoginButton(
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomCenter),
            onClickGoogle = { launcher.launch(client.signInIntent) }
        )

        if (loginUiState is LoginUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun LoginLogo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.feature_login_logo_ic),
            contentDescription = "Login Logo Icon",
        )

        Image(
            painter = painterResource(id = R.drawable.feature_login_logo),
            contentDescription = "Login Logo",
        )

        Text(
            text = stringResource(id = R.string.feature_login_logo_text),
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
private fun LoginButton(
    modifier: Modifier = Modifier,
    onClickGoogle: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "포착 시작하기",
                style = MaterialTheme.typography.titleSmall
            )

            Image(
                painter = painterResource(id = R.drawable.feature_login_google),
                contentDescription = "Google Login Button",
                modifier = Modifier
                    .clip(CircleShape)  // 클릭 ripple 효과를 위해 clip 사용
                    .clickable { onClickGoogle() }
            )
        }
    }
}

@Composable
private fun rememberGoogleLoginLauncher(
    context: Context,
    coroutineScope: CoroutineScope,
    onGoogleLogin: (String) -> Unit,
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
        val account = task.getResult(ApiException::class.java)

        val accountName = account?.email ?: return@rememberLauncherForActivityResult

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile"
                    val token = GoogleAuthUtil.getToken(context, accountName, scope)
                    Log.d(TAG, "Access Token: $token")
                    onGoogleLogin(token)
                } catch (e: Exception) {
                    Log.e(TAG, "Access Token Error: $e")
                }
            }
        }
    } catch (e: ApiException) {
        Log.d(TAG, "Google sign in failed", e)
    }
}
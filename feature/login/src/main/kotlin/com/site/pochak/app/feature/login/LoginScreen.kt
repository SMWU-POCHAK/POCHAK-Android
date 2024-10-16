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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.site.pochak.app.core.network.model.LoginInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private const val TAG = "LoginScreen"

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    navigateToSignUp: (String) -> Unit,
) {
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        onLoginSuccess = onLoginSuccess,
        loginUiState = loginUiState,
        onGoogleLogin = viewModel::googleLogin,
        resetLoginUiState = viewModel::resetLoginUiState,
        navigateToSignUp = navigateToSignUp,
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    loginUiState: LoginUiState,
    onGoogleLogin: (String) -> Unit,
    resetLoginUiState: () -> Unit,
    navigateToSignUp: (String) -> Unit,
) {
    LaunchedEffect(loginUiState) {
        if (loginUiState is LoginUiState.Success) {
            onLoginSuccess()
        }
        if (loginUiState is LoginUiState.SignUp) {
            navigateToSignUp(Json.encodeToString(LoginInfo.serializer(), loginUiState.loginInfo))

            // 회원 가입 창에서 로그인 화면으로 돌아왔을 때, 로그인 UI 상태를 초기화한다.
            resetLoginUiState()
        }
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberGoogleLoginLauncher(context, coroutineScope, onGoogleLogin)

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LoginLogo(modifier = Modifier.align(Alignment.Center))

        LoginButton(
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomCenter)
        ) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val client = GoogleSignIn.getClient(context, gso)
            launcher.launch(client.signInIntent)
        }
    }
}

@Composable
private fun LoginLogo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.feature_login_graphic_logo_ic),
            contentDescription = "Pochak Logo Icon"
        )

        Image(
            painter = painterResource(id = R.drawable.feature_login_graphic_logo),
            contentDescription = "Pochak Logo Text"
        )

        Text(
            text = "당신의 순간, 포착",
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
private fun LoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
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
                    .clip(CircleShape)
                    .clickable { onClick() }
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
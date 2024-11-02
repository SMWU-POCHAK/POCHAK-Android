package com.site.pochak.app.feature.login

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.site.pochak.app.core.network.model.NetworkLoginInfo
import kotlinx.serialization.json.Json

private const val TAG = "LoginScreen"

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
    onGoogleLogin: (GoogleSignInAccount?) -> Unit,
    resetLoginUiState: () -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberGoogleLoginLauncher(onGoogleLogin)
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val client = GoogleSignIn.getClient(context, options)

    LaunchedEffect(loginUiState) {
        Log.d(TAG, "LoginUiState: $loginUiState")
        when (loginUiState) {
            LoginUiState.Success -> navigateToHome()
            is LoginUiState.Error -> Log.e(TAG, "Login Error")
            is LoginUiState.SignUp -> {
                resetLoginUiState()

                navigateToSignUp(
                    Json.encodeToString(
                        NetworkLoginInfo.serializer(),
                        loginUiState.loginInfo
                    )
                )
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
            onClickGoogle = {
                client.revokeAccess().addOnCompleteListener {
                    launcher.launch(client.signInIntent)
                }
            }
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
                text = stringResource(id = R.string.feature_login_login_text),
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
private fun rememberGoogleLoginLauncher(onGoogleLogin: (GoogleSignInAccount?) -> Unit) =
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account: GoogleSignInAccount? = try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            Log.d(TAG, "Google sign in failed", e)
            null
        }

        onGoogleLogin(account)
    }

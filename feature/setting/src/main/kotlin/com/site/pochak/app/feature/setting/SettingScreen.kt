package com.site.pochak.app.feature.setting

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.site.pochak.app.core.designsystem.component.BackButton
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTextStyle
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.VerticalPadding
import com.site.pochak.app.core.designsystem.icon.PochakIcons
import com.site.pochak.app.core.designsystem.theme.Gray01

@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToBlockUser: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingScreen(
        modifier = modifier,
        uiState = uiState,
        onBack = onBack,
        logout = viewModel::logout,
        signOut = viewModel::signOut,
        navigateToLogin = navigateToLogin,
        navigateToBlockUser = navigateToBlockUser,
    )
}

@Composable
internal fun SettingScreen(
    modifier: Modifier = Modifier,
    uiState: SettingUiState,
    onBack: () -> Unit,
    navigateToBlockUser: () -> Unit,
    logout: () -> Unit,
    signOut: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is SettingUiState.Success) {
            navigateToLogin()
        }
        else if (uiState is SettingUiState.Idle) {
            uiState.errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT,).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        SettingContent(
            onBack = onBack,
            navigateToBlockUser = navigateToBlockUser,
            logout = logout,
            signOut = signOut,
        )

        if (uiState is SettingUiState.Loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
internal fun SettingContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    navigateToBlockUser: () -> Unit,
    logout: () -> Unit,
    signOut: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            leftContent = { BackButton(onClick = onBack) },
            centerContent = {
                Text(
                    text = "설정",
                    style = PochakTextStyle.body0,
                )
            },
        )

        LazyColumn {
            item { SettingItem(title = "이용약관") { } }
            item { SettingItem(title = "차단관리") { navigateToBlockUser() } }
            item { SettingItem(title = "로그아웃") { logout() } }
            item { SettingItem(title = "회원탈퇴") { signOut() } }
        }
    }
}

@Composable
internal fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = HorizontalPadding)
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = PochakTextStyle.body2,
            )

            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(PochakIcons.ArrowRight),
                    contentDescription = null,
                )
            }
        }

        HorizontalDivider(color = Gray01)
    }
}
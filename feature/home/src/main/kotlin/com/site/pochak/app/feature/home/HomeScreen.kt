package com.site.pochak.app.feature.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.component.RefreshableLazyVerticalGrid
import com.site.pochak.app.core.designsystem.theme.Gray02
import com.site.pochak.app.core.model.data.Post
import com.site.pochak.app.core.ui.postFeed

private const val TAG = "HomeScreen"

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homePosts = viewModel.homePosts.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        viewModel = viewModel,
        homePosts = homePosts.value,
        isLoading = isLoading.value,
        isRefreshing = isRefreshing.value,
        onLoadPage = viewModel::loadPage,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    RequestNotificationPermission(
        onPermissionGranted = {
            Log.d("HomeScreen", "Notification permission granted")
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "FCM Token: $token")
                    viewModel.registerFcmToken(token)
                } else {
                    Log.e("FCM", "Failed to fetch FCM token", task.exception)
                }
            }
        },
        onPermissionRevoked = {
            Log.d("HomeScreen", "Notification permission denied")
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        PochakTopAppBar(
            centerContent = {
                Image(
                    painter = painterResource(id = R.drawable.feature_home_logo_small),
                    contentDescription = "Pochak Logo",
                )
            }
        )

        HomeContent(
            modifier = Modifier.fillMaxSize(),
            homePosts = homePosts,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            onLoadPage = onLoadPage,
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    // Load the first page when the screen is launched
    LaunchedEffect(Unit) {
        if (homePosts.isEmpty() && !isLoading) {
            onLoadPage(true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (homePosts.isEmpty() && !isLoading) {
            HomePostNoPhoto(Modifier.align(Alignment.Center))
        } else {
            HomePostContent(
                modifier = Modifier.fillMaxSize(),
                homePosts = homePosts,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                onLoadPage = onLoadPage,
            )
        }
    }
}

@Composable
private fun HomePostNoPhoto(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.feature_home_no_photo),
            contentDescription = "No Posts",
        )

        Text(
            text = stringResource(id = R.string.feature_home_no_photo),
            style = MaterialTheme.typography.titleMedium.copy(color = Gray02),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomePostContent(
    modifier: Modifier = Modifier,
    homePosts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadPage: (Boolean) -> Unit,
) {
    RefreshableLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        refreshEnabled = true,
        isRefreshing = isRefreshing,
        columns = GridCells.Fixed(3),
        loadMore = onLoadPage,
    ) {
        postFeed(homePosts)

        if (isLoading && !isRefreshing) {
            item { }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionRevoked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionGrantedState = remember { mutableStateOf(false) }

    // Launcher 초기화
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionGrantedState.value = true
            onPermissionGranted()
        } else {
            permissionGrantedState.value = false
            onPermissionRevoked()
        }
    }

    // 권한 확인 및 요청
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (isPermissionGranted) {
                permissionGrantedState.value = true
                onPermissionGranted()
            } else if (activity != null) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 13 미만은 권한 필요 없음
            permissionGrantedState.value = true
            onPermissionGranted()
        }
    }
}

package com.site.pochak.app.feature.nearby.pochaker

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.site.pochak.app.core.designsystem.component.CircleCropAsyncImage
import com.site.pochak.app.core.designsystem.component.PochakNavigationTopAppBar
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
internal fun NearbyPochakerRoute(
    modifier: Modifier = Modifier,
    viewModel: NearbyPochakerViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToCamera: (String) -> Unit
) {
    val userHandle by viewModel.userHandle.collectAsState()
    val nearbyUsers by viewModel.nearbyUsers.collectAsState()

    NearbyPochakerScreen(
        modifier = modifier,
        viewModel = viewModel,
        userHandle = userHandle,
        nearbyUsers = nearbyUsers,
        onBack = onBack,
        navigateToCamera = navigateToCamera
    )
}

@SuppressLint("MissingPermission")
@Composable
fun NearbyPochakerScreen(
    modifier: Modifier = Modifier,
    viewModel: NearbyPochakerViewModel = hiltViewModel(),
    userHandle: String?,
    nearbyUsers: List<BluetoothDevice>,
    onBack: () -> Unit,
    navigateToCamera: (String) -> Unit
) {
    val context = LocalContext.current
    val userNames = nearbyUsers.map { it.name }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
        } else {
            Toast.makeText(context, "BLE 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !adapter.isEnabled) {
            context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PochakNavigationTopAppBar(
            onBack = onBack,
            title = stringResource(id = R.string.feature_nearby_pochaker_title),
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer { clip = false }
        ) {
            CircularBackground()

            // 주변 프로필 랜덤 위치로 배치
            RandomProfilesAroundCenter(
                profiles = userNames,
                excludeCenterRadius = 150.dp,
                modifier = Modifier.align(Alignment.Center),
                onClick = navigateToCamera
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // 프로필 이미지를 화면 정확히 중앙에 배치
                userHandle?.let { handle ->
                    ProfileImage(
                        handle = handle,
                        onClick = { navigateToCamera(handle) }
                    )
                }

                // 텍스트는 프로필 이미지 아래에 배치
                userHandle?.let {
                    Text(
                        text = "나",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 40.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "내 주변 포착 친구를 찾고, 포착하세요!",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CircularBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_nearby_pochaker),
            contentDescription = null,
            modifier = Modifier.size(656.dp),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun RandomProfilesAroundCenter(
    profiles: List<String>,
    excludeCenterRadius: Dp = 100.dp,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val density = LocalDensity.current
    val screenHeightDp = 484.dp
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val visibleProfiles = remember(profiles) { profiles.take(5) }

    val profileSizes = remember(visibleProfiles) {
        visibleProfiles.map { Random.nextInt(48, 61).dp }
    }

    val positions = remember(visibleProfiles) {
        val widthPx = with(density) { screenWidthDp.toPx() }
        val heightPx = with(density) { screenHeightDp.toPx() }
        val minGapPx = with(density) { 10.dp.toPx() }
        val random = Random(System.currentTimeMillis())

        val placedPositions = mutableListOf<Pair<Float, Float>>()
        val result = mutableListOf<ProfilePosition>()
        val maxAttempts = 30

        visibleProfiles.forEachIndexed { index, name ->
            val profileSize = profileSizes[index]
            val profileSizePx = with(density) { profileSize.toPx() }
            val radiusExclusionPx =
                with(density) { excludeCenterRadius.toPx() / 2f + profileSizePx / 2f }

            var attempt = 0
            var placed = false
            var offsetX = 0f
            var offsetY = 0f

            while (attempt < maxAttempts && !placed) {
                offsetX = random.nextFloat() * (widthPx - profileSizePx) - (widthPx - profileSizePx) / 2f
                offsetY = random.nextFloat() * (heightPx - profileSizePx) - (heightPx - profileSizePx) / 2f

                // 중심에서 너무 가까우면 제외
                val distanceFromCenter = hypot(offsetX, offsetY)
                if (distanceFromCenter < radiusExclusionPx) {
                    attempt++
                    continue
                }

                // 겹침 검사
                val overlaps = placedPositions.any { (x, y) ->
                    hypot(x - offsetX, y - offsetY) < profileSizePx + minGapPx
                }

                if (!overlaps) {
                    placedPositions.add(offsetX to offsetY)
                    placed = true
                }
                attempt++
            }

            result.add(ProfilePosition(name, offsetX, offsetY, profileSize))
        }

        result
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(656.dp),
        contentAlignment = Alignment.Center
    ) {
        positions.forEachIndexed { index, profile ->
            var visible by remember { mutableStateOf(false) }
            val alpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = index * 300),
                label = "fade-in"
            )

            LaunchedEffect(Unit) {
                delay(index * 300L)
                visible = true
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(profile.offsetX.roundToInt(), profile.offsetY.roundToInt()) }
                    .alpha(alpha)
            ) {
                ProfileWithLabel(
                    handle = profile.name,
                    size = profile.size,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ProfileWithLabel(
    modifier: Modifier = Modifier,
    handle: String,
    size: Dp,
    onClick: (String) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ProfileImage(
            handle = handle,
            size = size,
            onClick = onClick
        )
        Text(handle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ProfileImage(
    handle: String,
    size: Dp = 60.dp,
    onClick: (String) -> Unit = {}) {
    CircleCropAsyncImage(
        modifier = Modifier
            .size(size)
            .border(
            width = 2.dp,
            color = Color.White,
            shape = CircleShape
        ),
        contentDescription = "Owner Profile Image",
        imageUrl = "https://storage.googleapis.com/pochak-image-bucket/member/${handle}",
        onClick = { onClick(handle) }
    )
}

data class ProfilePosition(
    val name: String,
    val offsetX: Float,
    val offsetY: Float,
    val size: Dp
)
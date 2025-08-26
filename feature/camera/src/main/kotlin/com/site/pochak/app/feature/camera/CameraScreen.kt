package com.site.pochak.app.feature.camera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import android.view.ScaleGestureDetector
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.site.pochak.app.core.designsystem.component.HorizontalPadding
import com.site.pochak.app.core.designsystem.component.PochakTopAppBar
import com.site.pochak.app.core.designsystem.theme.Gray05
import com.site.pochak.app.core.designsystem.theme.Gray07
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

private const val TAG = "CameraScreen"

@Composable
internal fun CameraRoute(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    navigateToUpload: (String?) -> Unit,
) {
    CameraScreen(
        modifier = modifier,
        viewModel = viewModel,
        navigateToUpload = navigateToUpload
    )
}

@Composable
internal fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel,
    navigateToUpload: (String?) -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var permissionChecked by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var zoomState by remember { mutableStateOf<Float?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashOn by remember { mutableStateOf<Boolean>(false) }
    var selectedZoom by remember { mutableStateOf<Float?>(null) }
    var isAnimating by remember { mutableStateOf(false) }
    val nearbyPochakerHandle by viewModel.nearbyPochakerHandle.collectAsState()

    // 권한 요청 결과를 처리하는 Activity Result Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionGranted = isGranted
            permissionChecked = true
            if (isGranted) {
                zoomState = 1f // 줌 초기화
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraControl?.cancelFocusAndMetering() // 카메라 동작 중지
            cameraControl = null
            zoomState = null
            flashOn = false
        }
    }

    // Zoom 애니메이션 처리
    LaunchedEffect(selectedZoom) {
        selectedZoom?.let { targetZoom ->
            zoomState?.let { currentZoom ->
                animateZoom(cameraControl, currentZoom, targetZoom) { updatedZoom ->
                    zoomState = updatedZoom
                }
                isAnimating = false // 애니메이션 종료
            }
        }
    }
    
    // 초기 권한 상태 확인
    LaunchedEffect(Unit) {
        if (!permissionChecked) {
            permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                permissionChecked = true
                zoomState = 1f // 권한이 이미 허용된 경우 줌 초기화
            }
        }
    }

    if (permissionChecked && permissionGranted) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PochakTopAppBar(
                centerContent = {
                    val title = if (!nearbyPochakerHandle.isNullOrEmpty()) {
                        "@$nearbyPochakerHandle ${stringResource(R.string.feature_camera_title)}"
                    } else {
                        stringResource(R.string.feature_camera_title)
                    }

                    Text(text = title)
                },
            )
            Box(
                modifier = modifier
                    .padding(horizontal = HorizontalPadding)
                    .aspectRatio(3f / 4f)
            ) {
                // 카메라 미리보기 AndroidView
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                        }

                        // ScaleGestureDetector 생성
                        val scaleGestureDetector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                zoomState.let { currentZoomRatio ->
                                    val delta = detector.scaleFactor
                                    val newZoomRatio =
                                        (currentZoomRatio?.times(delta))?.coerceIn(0.5f, 6f)
                                    newZoomRatio?.let { cameraControl?.setZoomRatio(it) }
                                    zoomState = newZoomRatio
                                }
                                return true
                            }
                        })

                        // 터치 이벤트 처리
                        previewView.setOnTouchListener { view, event ->
                            scaleGestureDetector.onTouchEvent(event)
                            if (event.action == MotionEvent.ACTION_UP) {
                                view.performClick()
                            }
                            true
                        }

                        setCamera(previewView) { cameraControlInstance, initialZoomRatio, imageCaptureInstance ->
                            cameraControl = cameraControlInstance
                            zoomState = 1f
                            imageCapture = imageCaptureInstance
                        }

                        previewView
                    }
                )

                zoomState?.let {
                    Log.d(TAG, "Current Zoom ratio: $it")
                    CameraZoomOverlay(
                        currentZoom = it,
                        onZoomSelected = { selectedZoomRatio ->
                            Log.d(TAG, "Selected Zoom ratio: $selectedZoomRatio")
                            selectedZoom = selectedZoomRatio // 선택된 줌 배율 업데이트
                        },
                        onZoomStart = {
                            isAnimating = true // 애니메이션 시작
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        isAnimating = isAnimating
                    )
                }
            }

            CaptureControls(
                modifier = modifier,
                flashOn = flashOn,
                onCapture = {
                    takePhoto(context as Activity, imageCapture, flashOn) {
                        navigateToUpload(nearbyPochakerHandle)
                    }
                },
                onToggleFlash = {
                    flashOn = !flashOn
                }
            )
        }
    } else if (permissionChecked && !permissionGranted) {
        PermissionRequiredUI(modifier = modifier)
    }
}

@Composable
fun CameraZoomOverlay(
    modifier: Modifier = Modifier,
    currentZoom: Float,
    onZoomSelected: (Float) -> Unit,
    onZoomStart: () -> Unit, // 애니메이션 시작 콜백
    isAnimating: Boolean
) {
    val zoomOptions = listOf(0.5f, 1f, 2f, 3f)
    var isZoomOptionsVisible by remember { mutableStateOf(false) }
    var lastZoomChangeTime by remember { mutableStateOf(0L) }
    var initialLoadComplete by remember { mutableStateOf(false) }
    var displayZoom by remember { mutableStateOf(currentZoom) } // 애니메이션 상태에 따라 텍스트 고정

    val currentZoomIndex = when {
        currentZoom < 1 -> 0
        currentZoom < 2 -> 1
        currentZoom < 3 -> 2
        else -> 3
    }

    // 애니메이션 상태에 따라 표시 값 고정
    LaunchedEffect(isAnimating, currentZoom) {
        if (!isAnimating) {
            displayZoom = currentZoom // 애니메이션 종료 후 최종 값 반영
        }
    }

    // 자동 숨김 로직
    LaunchedEffect(currentZoom) {
        if (initialLoadComplete) {
            isZoomOptionsVisible = true
            lastZoomChangeTime = System.currentTimeMillis()

            delay(3000) // 3초 대기

            // 3초 동안 추가 변경이 없으면 리스트 숨김
            if (System.currentTimeMillis() - lastZoomChangeTime >= 3000) {
                isZoomOptionsVisible = false
            }
        } else {
            initialLoadComplete = true // 초기 로드 완료
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .width(if (isZoomOptionsVisible) 124.dp else 28.dp)
            .height(28.dp)
            .background(
                color = Gray05.copy(0.5f),
                shape = CircleShape
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 왼쪽 옵션들
        if (isZoomOptionsVisible) {
            zoomOptions.take(currentZoomIndex).forEach { zoom ->
                ZoomOptionItem(
                    zoom = zoom,
                    isSelected = false,
                    onZoomSelected = {
                        if (!isAnimating) {
                            onZoomStart() // 애니메이션 시작 알림
                            onZoomSelected(zoom)
                            lastZoomChangeTime = System.currentTimeMillis() // 시간 업데이트
                        }
                    }
                )
            }
        }

        // 현재 줌 배율 표시
        ZoomOptionItem(
            zoom = displayZoom,
            isSelected = true,
            onClick = {
                isZoomOptionsVisible = true
            }
        )

        // 오른쪽 옵션들
        if (isZoomOptionsVisible) {
            zoomOptions.drop(currentZoomIndex + 1).forEach { zoom ->
                ZoomOptionItem(
                    zoom = zoom,
                    isSelected = false,
                    onZoomSelected = {
                        if (!isAnimating) {
                            onZoomStart() // 애니메이션 시작 알림
                            onZoomSelected(zoom)
                            lastZoomChangeTime = System.currentTimeMillis() // 시간 업데이트
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ZoomOptionItem(
    zoom: Float,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onZoomSelected: ((Float) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = if (isSelected) Gray07.copy(alpha = 0.8f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onZoomSelected?.invoke(zoom) ?: onClick() },
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            modifier = Modifier.padding(end = 2.dp),
            text = "${"%.1f".format(zoom)}x",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun CaptureControls(
    modifier: Modifier = Modifier,
    flashOn: Boolean,
    onCapture: () -> Unit,
    onToggleFlash: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Centered capture button
        IconButton(
            onClick = onCapture,
            modifier = Modifier.size(62.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_capture_button),
                contentDescription = "Capture Button",
                modifier = Modifier.size(62.dp),
                tint = Color.Unspecified
            )
        }

        // Flash button 60dp to the right of the capture button
        IconButton(
            onClick = onToggleFlash,
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.Center)
                .offset(x = 91.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (flashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                ),
                contentDescription = "Flash Button",
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun PermissionRequiredUI(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.feature_camera_permission_camera),
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 카메라 미리보기 설정
 *
 * CameraX API(Preview)를 사용하여 카메라 미리보기 설정
 *
 * @param previewView 카메라 미리보기를 표시할 PreviewView, 실시간 카메라 영상을 렌더링.
 * @param onCameraControlAvailable CameraControl, 초기 줌 비율(Float), 이미지 캡처 기능(ImageCapture)을 제공하는 콜백 함수.
 */
private fun setCamera(
    previewView: PreviewView,
    onCameraControlAvailable: (CameraControl, Float, ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                previewView.context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            val cameraControl = camera.cameraControl
            val initialZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f

            onCameraControlAvailable(cameraControl, initialZoomRatio, imageCapture)
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Error starting camera", exc)
        }
    }, ContextCompat.getMainExecutor(previewView.context))
}

/**
 * 사진을 캡처하고, 저장된 이미지를 회전한 후 콜백을 호출하는 함수
 *
 * 카메라를 통해 사진을 캡처하고, Exif 정보를 기반으로 이미지를 올바른 방향으로 회전시킨 다음,
 * 회전된 이미지를 캐시 디렉토리에 저장, 기존에 저장된 이미지가 존재하는 경우, 삭제 후 새로운 이미지 저장
 *
 * @param activity 현재의 Activity 인스턴스, 사진 캡처 및 파일 작업에 사용
 * @param imageCapture ImageCapture 인스턴스, 사진 캡처 수행
 * @param onCapture 사진 캡처가 완료된 후 호출되는 콜백 함수
 *
 * @throws ImageCaptureException 사진 캡처 과정에서 오류가 발생한 경우 발생
 */
private fun takePhoto(
    activity: Activity,
    imageCapture: ImageCapture?,
    flashOn: Boolean,
    onCapture: () -> Unit
) {
    val outputDirectory = activity.cacheDir
    val photoFile = File(outputDirectory, "pochak_image.jpg")

    // 이전 파일이 존재하는 경우 삭제
    if (photoFile.exists()) {
        photoFile.delete()
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.let {
        // Set flash mode here before taking the picture
        when (flashOn) {
            true -> it.flashMode = ImageCapture.FLASH_MODE_ON
            false -> it.flashMode = ImageCapture.FLASH_MODE_OFF
        }

        it.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "사진 캡처 실패: ${exc.message}", exc)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val rotatedBitmap = getRotatedBitmap(photoFile) // 회전된 비트맵 가져오기

                    saveBitmapToFile(rotatedBitmap, photoFile)

                    onCapture()
                }
            }
        )
    }
}

/**
 * Exif 정보를 이용하여 이미지를 회전하고 반환합니다.
 *
 * @param file 회전된 비트맵을 가져올 파일, 절대 경로를 사용하여 이미지 파일을 가져옴
 * @return 회전된 비트맵
 */
private fun getRotatedBitmap(file: File): Bitmap {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    // Exif 정보 가져오기
    val exif = ExifInterface(file.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // 회전 각도 설정
    val rotationDegrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    // 비트맵 회전 처리
    val matrix = Matrix().apply {
        postRotate(rotationDegrees)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * 회전된 비트맵을 파일에 저장
 *
 * @param bitmap 저장할 비트맵
 * @param file 저장할 파일
 */
private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}

/**
 * 카메라 줌 비율을 시작 값에서 목표 값으로 애니메이션 효과를 통해 변경
 *
 * 이 함수는 줌 비율을 부드럽게 이동
 * 각 단계에서 카메라 줌 비율을 업데이트하고 제공된 콜백을 호출하여 현재 값 전달
 * 고정된 단계 수와 지속 시간을 사용하여 코루틴 컨텍스트에서 애니메이션 수행
 *
 * @param cameraControl 카메라의 줌 비율을 업데이트할 CameraControl 인스턴스.
 * @param startZoom 애니메이션 시작 시의 초기 줌 비율.
 * @param targetZoom 애니메이션 종료 시 목표로 하는 줌 비율.
 * @param onZoomUpdated 각 애니메이션 단계에서 업데이트된 줌 비율을 전달하는 콜백.
 *
 * @throws IllegalArgumentException `steps`나 `duration` 값이 0이거나 음수일 경우 발생.
 *
 */
suspend fun animateZoom(
    cameraControl: CameraControl?,
    startZoom: Float,
    targetZoom: Float,
    onZoomUpdated: (Float) -> Unit
) {
    val steps = 20 // 애니메이션 단계 수
    val duration = 150L // 애니메이션 전체 시간 (밀리초)
    val stepDuration = duration / steps
    val zoomDelta = (targetZoom - startZoom) / steps

    var currentZoom = startZoom
    repeat(steps) {
        currentZoom += zoomDelta
        cameraControl?.setZoomRatio(currentZoom)
        onZoomUpdated(currentZoom) // 상태 업데이트 콜백
        delay(stepDuration)
    }
    // 최종 줌 배율 설정
    cameraControl?.setZoomRatio(targetZoom)
    onZoomUpdated(targetZoom)
}
package com.site.pochak.app.feature.camera

import android.Manifest
import android.app.Activity
import android.content.Context
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import android.view.ScaleGestureDetector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import java.io.File
import java.io.FileOutputStream

private const val TAG = "CameraScreen"

@Composable
internal fun CameraRoute(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    navigateToUpload: () -> Unit,
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
    navigateToUpload: () -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var permissionChecked by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var zoomState by remember { mutableStateOf<Float?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashOn by remember { mutableStateOf<Boolean>(false) }  // Flash state

    LaunchedEffect(Unit) {
        if (!permissionChecked) {
            permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    0
                )
            }
            permissionChecked = true
        }
    }

    if (permissionChecked && permissionGranted) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    }

                    // ScaleGestureDetector 생성
                    val scaleGestureDetector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            zoomState?.let { currentZoomRatio ->
                                val delta = detector.scaleFactor
                                val newZoomRatio = currentZoomRatio * delta
                                cameraControl?.setZoomRatio(newZoomRatio.coerceIn(0.5f, 6f))
                                zoomState = newZoomRatio.coerceIn(0.5f, 6f)
                            }
                            return true
                        }
                    })

                    // 터치 이벤트 처리
                    previewView.setOnTouchListener { view, event ->
                        // Scale gesture 처리
                        scaleGestureDetector.onTouchEvent(event)

                        if (event.action == MotionEvent.ACTION_UP) {
                            view.performClick()
                        }

                        true
                    }

                    setCamera(previewView) { cameraControlInstance, initialZoomRatio, imageCaptureInstance ->
                        cameraControl = cameraControlInstance
                        zoomState = initialZoomRatio
                        imageCapture = imageCaptureInstance
                    }

                    previewView
                },
                modifier = Modifier
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                    .aspectRatio(3f / 4f)
            )

            // Capture Button and Flash Toggle
            CaptureAndFlashButton(
                zoomState = zoomState,
                flashOn = flashOn,
                onCapture = {
                    takePhoto(context as Activity, imageCapture, flashOn) {
                        navigateToUpload() // Navigate to upload screen
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
private fun CaptureAndFlashButton(
    zoomState: Float?,
    flashOn: Boolean,
    onCapture: () -> Unit,
    onToggleFlash: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(60.dp)
        ) {
            zoomState?.let { zoom ->
                Text(
                    text = "${"%.1f".format(zoom)}x",
                )
            }

            IconButton(
                onClick = onCapture,
                modifier = Modifier
                    .size(62.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_capture_button),
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(62.dp),
                    tint = Color.Unspecified
                )
            }

            IconButton(
                onClick = onToggleFlash,
                modifier = Modifier
                    .size(34.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (flashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                    ),
                    contentDescription = "Flash Icon",
                    tint = Color.Unspecified
                )
            }
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
            text = "카메라 권한이 필요합니다.\n설정에서 권한을 허용해주세요.",
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 카메라 미리보기 설정
 *
 * CameraX API(Preview)를 사용하여 카메라 미리보기 설정
 *
 * @param previewView 카메라 미리보기를 표시할 PreviewView, 카메라의 실시간 영상을 렌더링
 * @param onCameraControlAvailable 카메라 제어 객체(CameraControl), 초기 줌 비율(Float), 이미지 캡처 기능(ImageCapture)을 제공하는 콜백 함수.
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
 * 이 함수는 카메라를 통해 사진을 캡처하고, Exif 정보를 기반으로 이미지를 올바른 방향으로 회전시킨 다음,
 * 회전된 이미지를 캐시 디렉토리에 저장
 * 이전에 저장된 이미지가 존재하는 경우, 삭제 후 새로운 이미지 저장
 *
 * @param activity 현재의 Activity 인스턴스, 이 인스턴스는 사진 캡처 및 파일 작업에 사용
 * @param imageCapture ImageCapture 인스턴스, 이 인스턴스를 통해 사진 캡처 수행
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
 * Exif 정보를 이용하여 이미지를 회전 처리하고 반환합니다.
 *
 * @param file 회전된 비트맵을 가져올 파일, 절대 경로를 사용하여 이미지 파일을 가져옴
 * @return 회전된 비트맵 반환
 */
fun getRotatedBitmap(file: File): Bitmap {
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
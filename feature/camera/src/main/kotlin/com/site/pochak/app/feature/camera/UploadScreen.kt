package com.site.pochak.app.feature.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@Composable
internal fun UploadRoute(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    UploadScreen(
        modifier = modifier,
        navigateToHome = navigateToHome,
        onBackClick = onBackClick,
    )
}

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    // 캐시 디렉토리에서 이미지 로드
    val context = LocalContext.current
    val cachedImageFile = File(context.cacheDir, "pochak_image.jpg")

    var capturedImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    if (cachedImageFile.exists()) {
        capturedImageBitmap = BitmapFactory.decodeFile(cachedImageFile.absolutePath)
    }

    if (capturedImageBitmap != null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Image(
                    bitmap = capturedImageBitmap!!.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onBackClick) {
                    Text("Retake")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = navigateToHome) {
                    Text("Upload")
                }
            }
        }
    } else {
        Text("No image captured")
    }
}


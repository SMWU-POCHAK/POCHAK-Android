package com.site.pochak.app.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Uri를 압축된 File로 변환
 * POST 과정에서 압축된 이미지 필요
 *
 * @param uri       변환할 Uri
 * @param context   Context
 * @param quality   압축 품질 (기본값: 80)
 * @return          변환된 File
 */
fun uriToFile(uri: Uri, context: Context, quality: Int = 60): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val file = File.createTempFile("profile", ".jpeg", context.cacheDir)

    val rotatedBitmap = rotateImageIfNeeded(bitmap, context, uri)

    FileOutputStream(file).use { output ->
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
    }

    return file
}

fun compressImageFile(inputFile: File, context: Context, quality: Int = 50): File {
    // Decode the bitmap from the file
    val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath)

    // Create a temporary file in the cache directory
    val compressedFile = File.createTempFile("compressed_image", ".jpeg", context.cacheDir)

    // Compress and save the bitmap to the new file
    FileOutputStream(compressedFile).use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
    }

    return compressedFile // Return the created file
}

fun fileToMultiPartBody(file: File, name: String, mediaType: String = "image/jpeg"): MultipartBody.Part {
    val requestBody = RequestBody.create(mediaType.toMediaTypeOrNull(), file)

    return MultipartBody.Part.createFormData(name, file.name, requestBody)
}

private fun rotateImageIfNeeded(bitmap: Bitmap, context: Context, uri: Uri): Bitmap {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exifInterface = ExifInterface(inputStream!!)
        val rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap  // 회전이 필요 없으면 원본 이미지 반환
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bitmap
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = android.graphics.Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
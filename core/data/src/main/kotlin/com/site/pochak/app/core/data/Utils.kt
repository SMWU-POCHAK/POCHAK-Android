package com.site.pochak.app.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Uri를 압축된 File로 변환
 * POST 과정에서 압축된 이미지 필요
 *
 * @param uri       변환할 Uri
 * @param context   Context
 * @param quality   압축 품질 (기본값: 80)
 * @return          변환된 File
 */
fun uriToFile(uri: Uri, context: Context, quality: Int = 80): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val file = File.createTempFile("profile", ".jpeg", context.cacheDir)

    FileOutputStream(file).use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
    }

    return file
}
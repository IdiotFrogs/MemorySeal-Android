package com.idiotfrogs.extension

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/** name은 key 값과 동일해야 함 */
fun Uri.toFile(context: Context, name: String): File? {
    val inputStream = context.contentResolver.openInputStream(this) ?: return null
    val currentMillis = System.currentTimeMillis()
    val tempFile = File(context.cacheDir, "MS$name$currentMillis.jpg")
    val outputStream = FileOutputStream(tempFile)

    inputStream.copyTo(outputStream)

    // 리소스 누수 방지
    inputStream.close()
    outputStream.close()

    return tempFile
}
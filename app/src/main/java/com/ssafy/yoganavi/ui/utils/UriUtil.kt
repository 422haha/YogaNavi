package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

fun getVideoPath(context: Context, uri: Uri): String {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
    returnCursor?.moveToFirst()
    val name = returnCursor?.getString(nameIndex) ?: ""
    val file = File(context.filesDir, name)
    returnCursor?.close()

    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val maxBufferSize = 1024 * 1024
                val buffer = ByteArray(maxBufferSize)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return file.path
}

fun getImagePath(context: Context, uri: Uri): String {
    val contentResolver = context.contentResolver
    val returnCursor = contentResolver.query(uri, null, null, null, null)

    val name = returnCursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameIndex)
    } ?: return ""

    val originalFile = File(context.filesDir, name)

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(originalFile).use { outputStream ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }

    val bitmap = BitmapFactory.decodeFile(originalFile.path) ?: return ""

    val webpFile = File(context.filesDir, "${name.substringBeforeLast('.')}.webp")
    try {
        FileOutputStream(webpFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    } finally {
        bitmap.recycle()
    }

    originalFile.delete()

    return webpFile.path
}


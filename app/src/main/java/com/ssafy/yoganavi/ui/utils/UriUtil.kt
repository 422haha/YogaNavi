package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

suspend fun getImagePath(
    context: Context,
    uri: Uri,
    ratio: Int = 1
): String = withContext(Dispatchers.IO) {
    val contentResolver = context.contentResolver
    val returnCursor = contentResolver.query(uri, null, null, null, null)

    val name = returnCursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        it.moveToFirst()
        it.getString(nameIndex)
    } ?: return@withContext ""

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
        return@withContext ""
    }

    val bmp = BitmapFactory.decodeFile(originalFile.path) ?: return@withContext ""
    val rotatedBitmap = getCorrectlyOrientedBitmap(originalFile.path, bmp)

    val bitmap = Bitmap.createScaledBitmap(
        rotatedBitmap,
        rotatedBitmap.width / ratio,
        rotatedBitmap.height / ratio,
        true
    )

    val webpFile = File(context.filesDir, "${name.substringBeforeLast('.')}.webp")
    try {
        FileOutputStream(webpFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 70, outputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext ""
    } finally {
        bitmap.recycle()
        rotatedBitmap.recycle()
        bmp.recycle()
    }

    originalFile.delete()

    return@withContext webpFile.path
}

fun getCorrectlyOrientedBitmap(filePath: String, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(filePath)
    val orientation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val matrix = android.graphics.Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
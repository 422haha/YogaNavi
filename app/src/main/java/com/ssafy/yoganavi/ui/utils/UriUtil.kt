package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

fun getPath(context: Context, uri: Uri): String {
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

package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore


fun getPath(context: Context, contentUri: Uri): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(contentUri, proj, null, null, null) ?: return ""
    cursor.moveToNext()
    val data = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
    if (data == -1) return ""
    val path = cursor.getString(data)
    cursor.close()
    return path
}

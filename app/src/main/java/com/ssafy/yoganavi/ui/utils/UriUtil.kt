package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore


fun getPath(context: Context, contentUri: Uri): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(contentUri, proj, null, null, null) ?: return ""
    cursor.moveToNext()
    val path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
//    val uri = Uri.fromFile(File(path))
    cursor.close()
    return path
}
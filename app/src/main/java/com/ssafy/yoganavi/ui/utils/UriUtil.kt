package com.ssafy.yoganavi.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.exifinterface.media.ExifInterface
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.model.ObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.math.sqrt

const val MB = 1_048_576
fun getVideoPath(context: Context, uri: Uri): String {
    val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: ""

    if (fileName.isBlank()) return ""
    val yogaDir = getYogaDirectory(context)
    val file = File(yogaDir, fileName)
    file.createNewFile()

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } ?: return ""

    return file.path
}

suspend fun getImagePath(
    context: Context,
    uri: Uri
): Pair<String, String> = withContext(Dispatchers.IO) {
    val contentResolver = context.contentResolver

    // 파일 이름 가져오기
    val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: ""
    if (fileName.isBlank()) return@withContext Pair("", "")

    // 임시 파일 생성
    val yogaDir = getYogaDirectory(context)
    val tempFile = File(yogaDir, fileName)
    tempFile.createNewFile()

    contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } ?: return@withContext Pair("", "")

    val originalPath = tempFile.path

    val bmp = BitmapFactory.decodeFile(originalPath) ?: return@withContext Pair("", "")
    val rotatedBitmap = getCorrectlyOrientedBitmap(originalPath, bmp)

    val fileSizeInBytes = tempFile.length()
    val scaleFactor = if (fileSizeInBytes > MB) {
        sqrt(MB.toDouble() / fileSizeInBytes.toDouble())
    } else {
        1.0
    }

    val newWidth = (rotatedBitmap.width * scaleFactor).toInt()
    val newHeight = (rotatedBitmap.height * scaleFactor).toInt()
    val miniBitmap = Bitmap.createScaledBitmap(
        rotatedBitmap,
        newWidth,
        newHeight,
        true
    )

    val miniWebpFile = File(yogaDir, "${fileName.substringBeforeLast('.')}_mini.webp")
    try {
        FileOutputStream(miniWebpFile).use { outputStream ->
            miniBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext Pair("", "")
    } finally {
        miniBitmap.recycle()
        rotatedBitmap.recycle()
        bmp.recycle()
    }

    return@withContext Pair(originalPath, miniWebpFile.path)
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

suspend fun uploadFile(
    transferUtility: TransferUtility,
    key: String?,
    file: File,
    meta: ObjectMetadata? = null
): Boolean = suspendCancellableCoroutine { continuation ->
    val observer = meta?.let {
        transferUtility.upload(BUCKET_NAME, key, file, meta)
    } ?: transferUtility.upload(BUCKET_NAME, key, file)

    observer.setTransferListener(object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) continuation.resume(true)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        }

        override fun onError(id: Int, ex: java.lang.Exception?) {
            continuation.resume(false)
        }
    })
}

suspend fun downloadFile(
    transferUtility: TransferUtility,
    key: String,
    file: File
): Boolean = suspendCancellableCoroutine { continuation ->
    val observer = transferUtility.download(BUCKET_NAME, key, file)

    observer.setTransferListener(object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) continuation.resume(true)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        }

        override fun onError(id: Int, ex: java.lang.Exception?) {
            continuation.resume(false)
        }
    })
}

fun getYogaDirectory(context: Context): File {
    val yogaDir = File(context.cacheDir, YOGA)
    if (!yogaDir.exists()) yogaDir.mkdirs()
    return yogaDir
}
package com.ssafy.yoganavi.data.repository.ai

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

interface PoseRepository {

    suspend fun infer(image: ImageProxy, width: Int, height: Int) : List<FloatArray>

    suspend fun infer(bitmap: Bitmap, width: Int, height: Int) : List<FloatArray>
}
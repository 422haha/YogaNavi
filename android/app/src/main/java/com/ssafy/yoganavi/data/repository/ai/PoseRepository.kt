package com.ssafy.yoganavi.data.repository.ai

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.ssafy.yoganavi.data.source.ai.KeyPoint

interface PoseRepository {

    suspend fun infer(image: ImageProxy, width: Int, height: Int) : List<List<KeyPoint>>

    suspend fun infer(bitmap: Bitmap, width: Int, height: Int) : List<List<KeyPoint>>
}
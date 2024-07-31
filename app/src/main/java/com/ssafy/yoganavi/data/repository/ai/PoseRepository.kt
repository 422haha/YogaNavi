package com.ssafy.yoganavi.data.repository.ai

import androidx.camera.core.ImageProxy

interface PoseRepository {

    suspend fun infer(image: ImageProxy, width: Int, height: Int) : List<FloatArray>

}
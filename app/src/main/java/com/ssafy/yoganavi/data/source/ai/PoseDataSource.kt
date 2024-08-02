package com.ssafy.yoganavi.data.source.ai

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

interface PoseDataSource {

    suspend fun infer(image: ImageProxy, width: Int, height: Int): List<List<KeyPoint>>

    suspend fun infer(bitmap: Bitmap, width: Int, height: Int): List<List<KeyPoint>>

    fun preProcess(bitmap: Bitmap, needFlip: Boolean): OnnxTensor

    fun process(inputTensor: OnnxTensor): OrtSession.Result

    fun postProcess(rawOutput: OrtSession.Result): List<FloatArray>

    fun rescaleToCamera(result: List<FloatArray>, width: Int, height: Int): List<FloatArray>

    fun rescaleToBitmap(result: List<FloatArray>, width: Int, height: Int): List<FloatArray>

}

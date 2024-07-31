package com.ssafy.yoganavi.data.source.ai

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import androidx.camera.core.ImageProxy

interface PoseDataSource {

    suspend fun infer(imageProxy: ImageProxy, width: Int, height: Int) : List<FloatArray>

    fun preProcess(imageProxy: ImageProxy) : OnnxTensor

    fun process(inputTensor: OnnxTensor) : OrtSession.Result

    fun postProcess(rawOutput: OrtSession.Result) : List<FloatArray>

    fun rescale(result: List<FloatArray>, width: Int, height: Int) : List<FloatArray>

}

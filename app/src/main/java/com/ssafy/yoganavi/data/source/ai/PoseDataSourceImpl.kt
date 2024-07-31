package com.ssafy.yoganavi.data.source.ai

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import androidx.camera.core.ImageProxy
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.ui.utils.CENTER_X
import com.ssafy.yoganavi.ui.utils.CENTER_Y
import com.ssafy.yoganavi.ui.utils.HEIGHT
import com.ssafy.yoganavi.ui.utils.HEIGHT_RATIO
import com.ssafy.yoganavi.ui.utils.SCORE
import com.ssafy.yoganavi.ui.utils.WIDTH
import com.ssafy.yoganavi.ui.utils.WIDTH_RATIO
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class PoseDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PoseDataSource {

    private val confidenceThreshold = 0.45f
    private val iouThreshold = 0.5f
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession = ortEnv.createSession(readModel())
    private val shape = (ortSession.inputInfo["images"]?.info as TensorInfo).shape
    private val modelW = shape[3].toInt()
    private val modelH = shape[2].toInt()
    private val imageStd = 255f

    override suspend fun infer(imageProxy: ImageProxy, width: Int, height: Int): List<FloatArray> {
        val inputTensor = preProcess(imageProxy)
        val rawOutput = process(inputTensor)
        val output = postProcess(rawOutput)
        return rescale(output, width, height)
    }

    override fun preProcess(imageProxy: ImageProxy): OnnxTensor {
        val matrix = Matrix().apply { setScale(-1f, 1f) }
        val bitmap = imageProxy.toBitmap()
        val rescaledBitmap = Bitmap.createScaledBitmap(bitmap, modelW, modelH, true)
        val rotatedBitmap = Bitmap.createBitmap(rescaledBitmap, 0, 0, modelW, modelH, matrix, true)

        val cap = shape.reduce { acc, l -> acc * l }.toInt()
        val order = ByteOrder.nativeOrder()
        val buffer = ByteBuffer.allocateDirect(cap * Float.SIZE_BYTES).order(order).asFloatBuffer()
        val area = modelW * modelH

        for (x in 0 until modelW) {
            for (y in 0 until modelH) {
                val idx = modelW * y + x
                val pixelValue = rotatedBitmap.getPixel(x, y)

                buffer.put(idx, Color.red(pixelValue) / imageStd)
                buffer.put(idx + area, Color.green(pixelValue) / imageStd)
                buffer.put(idx + area * 2, Color.blue(pixelValue) / imageStd)
            }
        }
        return OnnxTensor.createTensor(ortEnv, buffer, shape)
    }

    override fun process(inputTensor: OnnxTensor): OrtSession.Result = inputTensor.use {
        val inputName = ortSession.inputNames.first()
        return ortSession.run(Collections.singletonMap(inputName, inputTensor))
    }

    override fun postProcess(rawOutput: OrtSession.Result): List<FloatArray> {
        val outputs = rawOutput[0].value as Array<*>
        val filteredOutputs = filterByConfidence(outputs[0] as Array<*>)
        return filterByIOU(filteredOutputs)
    }

    override fun rescale(result: List<FloatArray>, width: Int, height: Int): List<FloatArray> {
        val scaleX = width / modelW.toFloat()
        val scaleY = scaleX * HEIGHT_RATIO / WIDTH_RATIO
        val realY = width * HEIGHT_RATIO / WIDTH_RATIO
        val diffY = realY - height

        result.forEach { keyPoints ->
            for (idx in 5 until keyPoints.size step 3) {
                keyPoints[idx] = keyPoints[idx] * scaleX
                keyPoints[idx + 1] = keyPoints[idx + 1] * scaleY - (diffY / 2f)
            }
        }

        return result
    }

    private fun filterByConfidence(outputs: Array<*>): List<FloatArray> {
        val filteredOutputs = mutableListOf<FloatArray>()
        val rows = outputs.size
        val cols = if (rows > 0) (outputs[0] as FloatArray).size else 0

        val scoreArray = outputs[SCORE] as FloatArray
        for (i in 0 until cols) {
            if (scoreArray[i] < confidenceThreshold) continue
            val output = FloatArray(rows)

            for (j in 0 until rows) {
                output[j] = (outputs[j] as FloatArray)[i]
            }
            filteredOutputs.add(output)
        }

        return filteredOutputs
    }

    private fun filterByIOU(outputList: List<FloatArray>): List<FloatArray> {
        val filteredOutputs = mutableListOf<FloatArray>()
        val sortedList = outputList.sortedByDescending { it[SCORE] }

        for (i in sortedList.indices) {
            var keep = true
            val left = sortedList[i][CENTER_X] - sortedList[i][WIDTH] / 2
            val right = sortedList[i][CENTER_X] + sortedList[i][WIDTH] / 2
            val top = sortedList[i][CENTER_Y] - sortedList[i][HEIGHT] / 2
            val height = sortedList[i][CENTER_Y] + sortedList[i][HEIGHT] / 2
            val currentBox = RectF(left, top, right, height)

            for (j in 0 until filteredOutputs.size) {
                val previousLeft = filteredOutputs[j][CENTER_X] - filteredOutputs[j][WIDTH] / 2
                val previousRight = filteredOutputs[j][CENTER_X] + filteredOutputs[j][WIDTH] / 2
                val previousTop = filteredOutputs[j][CENTER_Y] - filteredOutputs[j][HEIGHT] / 2
                val previousHeight = filteredOutputs[j][CENTER_Y] + filteredOutputs[j][HEIGHT] / 2
                val previousBox = RectF(previousLeft, previousTop, previousRight, previousHeight)
                val iou = getIOU(currentBox, previousBox)

                if (iou > iouThreshold) {
                    keep = false
                    break
                }
            }

            if (keep) filteredOutputs.add(sortedList[i])
        }

        return filteredOutputs
    }

    private fun getIntersection(rect1: RectF, rect2: RectF): RectF {
        val left = max(rect1.left, rect2.left)
        val top = max(rect1.top, rect2.top)
        val right = min(rect1.right, rect2.right)
        val bottom = min(rect1.bottom, rect2.bottom)
        return RectF(left, top, right, bottom)
    }

    private fun getArea(rect: RectF): Float {
        return rect.width() * rect.height()
    }

    private fun getUnion(rect1: RectF, rect2: RectF, intersectionArea: Float): Float {
        return getArea(rect1) + getArea(rect2) - intersectionArea
    }

    private fun getIOU(rect1: RectF, rect2: RectF): Float {
        val intersection = getIntersection(rect1, rect2)
        val intersectionArea = getArea(intersection)

        if (intersectionArea <= 0) return 0f

        val unionArea = getUnion(rect1, rect2, intersectionArea)
        return intersectionArea / unionArea
    }

    private fun readModel(): ByteArray = context.resources
        .openRawResource(R.raw.yolov8n_pose)
        .readBytes()

}

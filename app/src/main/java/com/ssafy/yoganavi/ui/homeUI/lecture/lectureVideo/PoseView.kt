package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ssafy.yoganavi.data.source.ai.KeyPoint
import com.ssafy.yoganavi.ui.utils.KEYPOINT_NUM
import com.ssafy.yoganavi.ui.utils.LEFT_ANKLE
import com.ssafy.yoganavi.ui.utils.LEFT_ELBOW
import com.ssafy.yoganavi.ui.utils.LEFT_HIP
import com.ssafy.yoganavi.ui.utils.LEFT_KNEE
import com.ssafy.yoganavi.ui.utils.LEFT_SHOULDER
import com.ssafy.yoganavi.ui.utils.LEFT_WRIST
import com.ssafy.yoganavi.ui.utils.RIGHT_ANKLE
import com.ssafy.yoganavi.ui.utils.RIGHT_ELBOW
import com.ssafy.yoganavi.ui.utils.RIGHT_HIP
import com.ssafy.yoganavi.ui.utils.RIGHT_KNEE
import com.ssafy.yoganavi.ui.utils.RIGHT_SHOULDER
import com.ssafy.yoganavi.ui.utils.RIGHT_WRIST
import kotlin.math.pow
import kotlin.math.sqrt

class PoseView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val size = KEYPOINT_NUM / 3
    private val distanceThreshold = 200f
    private val keyPointsThreshold = 0.45f
    private val radius = 15f
    private var userKeyPoints: List<KeyPoint> = List(size) { KeyPoint(0, 0f, 0f, 0f) }
    private var teacherKeyPoints: List<KeyPoint> = List(size) { KeyPoint(0, 0f, 0f, 0f) }
    private val colorList: MutableList<Int> = MutableList(size) { Color.GREEN }

    private val goodPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    private val badPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    override fun onDraw(canvas: Canvas) {
        drawPointsAndLines(canvas)
        super.onDraw(canvas)
    }

    private fun drawPointsAndLines(canvas: Canvas) {
        drawPoints(canvas)
        drawLines(canvas)
    }

    private fun drawPoints(canvas: Canvas) {
        userKeyPoints.forEachIndexed { index, kpt ->
            if (kpt.x <= 0 || kpt.y <= 0 || kpt.confidence < keyPointsThreshold) return@forEachIndexed
            if (colorList[index] == Color.GREEN) canvas.drawCircle(kpt.x, kpt.y, radius, goodPaint)
            else canvas.drawCircle(kpt.x, kpt.y, radius, badPaint)
        }
    }

    private fun drawLines(canvas: Canvas) {
        if (userKeyPoints.isEmpty()) return

        drawArmLines(canvas)
        drawLegLines(canvas)
        drawBodyLines(canvas)
    }

    private fun compareWithTeacher() {
        userKeyPoints.forEachIndexed { idx, _ ->
            val userX = userKeyPoints[idx].x
            val userY = userKeyPoints[idx].y
            val teacherX = teacherKeyPoints[idx].x
            val teacherY = teacherKeyPoints[idx].y

            val distanceX = (userX - teacherX).toDouble().pow(2.0)
            val distanceY = (userY - teacherY).toDouble().pow(2.0)
            val distance = sqrt(distanceX + distanceY)

            colorList[idx] = if (distance > distanceThreshold) Color.RED else Color.GREEN
        }
    }

    private fun drawArmLines(canvas: Canvas) {
        val leftShoulder = userKeyPoints[LEFT_SHOULDER]
        val leftElbow = userKeyPoints[LEFT_ELBOW]
        val leftWrist = userKeyPoints[LEFT_WRIST]

        val rightShoulder = userKeyPoints[RIGHT_SHOULDER]
        val rightElbow = userKeyPoints[RIGHT_ELBOW]
        val rightWrist = userKeyPoints[RIGHT_WRIST]

        drawLine(canvas, leftShoulder, leftElbow)
        drawLine(canvas, leftElbow, leftWrist)
        drawLine(canvas, rightShoulder, rightElbow)
        drawLine(canvas, rightElbow, rightWrist)
    }

    private fun drawLegLines(canvas: Canvas) {
        val leftHip = userKeyPoints[LEFT_HIP]
        val leftKnee = userKeyPoints[LEFT_KNEE]
        val leftAnkle = userKeyPoints[LEFT_ANKLE]

        val rightHip = userKeyPoints[RIGHT_HIP]
        val rightKnee = userKeyPoints[RIGHT_KNEE]
        val rightAnkle = userKeyPoints[RIGHT_ANKLE]

        drawLine(canvas, leftHip, leftKnee)
        drawLine(canvas, leftKnee, leftAnkle)
        drawLine(canvas, rightHip, rightKnee)
        drawLine(canvas, rightKnee, rightAnkle)
    }

    private fun drawBodyLines(canvas: Canvas) {
        val leftShoulder = userKeyPoints[LEFT_SHOULDER]
        val leftHip = userKeyPoints[LEFT_HIP]
        val rightShoulder = userKeyPoints[RIGHT_SHOULDER]
        val rightHip = userKeyPoints[RIGHT_HIP]

        drawLine(canvas, leftShoulder, rightShoulder)
        drawLine(canvas, leftShoulder, leftHip)
        drawLine(canvas, rightShoulder, rightHip)
        drawLine(canvas, leftHip, rightHip)
    }

    private fun drawLine(canvas: Canvas, start: KeyPoint, end: KeyPoint) {
        if (start.x <= 0 || start.y <= 0 || end.x <= 0 || end.y <= 0) return
        if (start.confidence < keyPointsThreshold || end.confidence < keyPointsThreshold) return

        if(colorList[start.index] == Color.GREEN && colorList[end.index] == Color.GREEN){
            canvas.drawLine(start.x, start.y, end.x, end.y, goodPaint)
        }else{
            canvas.drawLine(start.x, start.y, end.x, end.y, badPaint)
        }
    }

    fun updateUserKeyPoints(newKeyPoints: List<KeyPoint>) {
        userKeyPoints = newKeyPoints
        invalidate()
    }

    fun updateTeacherKeyPoints(newKeyPoints: List<KeyPoint>) {
        teacherKeyPoints = newKeyPoints
        compareWithTeacher()
    }
}

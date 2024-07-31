package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PoseView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val kPointsThreshold = 0.45f
    private val radius = 15f
    private var userKeyPoints: List<FloatArray> = emptyList()
    private var teacherKeyPoints: List<FloatArray> = emptyList()

    private val userPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    override fun onDraw(canvas: Canvas) {
        drawPointsAndLines(canvas)
        super.onDraw(canvas)
    }

    private fun drawPointsAndLines(canvas: Canvas) {
        userKeyPoints.forEach { points ->
            drawPoint(canvas, points)
        }
    }

    private fun drawPoint(canvas: Canvas, points: FloatArray) {
        for (i in points.indices step 3) {
            val xPos = points[i]
            val yPos = points[i + 1]
            val confidence = points[i + 2]
            if (xPos > 0 && yPos > 0 && confidence > kPointsThreshold) {
                canvas.drawCircle(xPos, yPos, radius, userPaint)
            }
        }
    }

    fun updateUserKeyPoints(newKeyPoints: List<FloatArray>) {
        userKeyPoints = newKeyPoints
        invalidate()
    }

    fun updateTeacherKeyPoints(newKeyPoints: List<FloatArray>) {
        teacherKeyPoints = newKeyPoints
        // TODO 비교 연산 해야함!
    }
}

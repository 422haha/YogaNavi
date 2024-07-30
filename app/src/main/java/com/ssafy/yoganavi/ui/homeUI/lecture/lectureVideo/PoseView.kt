package com.ssafy.yoganavi.ui.homeUI.lecture.lectureVideo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PoseView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val kPointsThreshold = 0.35f
    private var keyPoints: List<FloatArray> = emptyList()
    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    override fun onDraw(canvas: Canvas) {
        drawPointsAndLines(canvas)
        super.onDraw(canvas)
    }

    private fun drawPointsAndLines(canvas: Canvas) {
        keyPoints.forEach { points ->
            drawPoint(canvas, points)
        }
    }

    private fun drawPoint(canvas: Canvas, points: FloatArray) {
        for (i in points.indices step 3) {
            val xPos = points[i]
            val yPos = points[i + 1]
            val confidence = points[i + 2]
            if (xPos > 0 && yPos > 0 && confidence > kPointsThreshold) {
                canvas.drawPoint(xPos, yPos, pointPaint)
            }
        }
    }

    fun updateKeyPoints(newKeyPoints: List<FloatArray>) {
        keyPoints = newKeyPoints
        invalidate()
    }
}
package com.ssafy.yoganavi.ui.homeUI.schedule.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ssafy.yoganavi.R

class DashedDividerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.surface_container) // 원하는 색상으로 변경 가능
        style = Paint.Style.STROKE
        strokeWidth = 3f // 원하는 두께로 변경 가능
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(15f, 15f), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, paint)
    }
}

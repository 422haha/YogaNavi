package com.ssafy.yoganavi.ui.utils

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(milliseconds: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(milliseconds)
    return formatter.format(date)
}

fun formatDotDate(milliseconds: Long): String {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    val date = Date(milliseconds)
    return formatter.format(date)
}

fun formatDashDate(milliseconds: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(milliseconds)
    return formatter.format(date)
}

fun formatZeroDate(hour: Int, minute: Int): String {
    val hourStr: String = if(hour < 10) "0$hour" else "$hour"

    val minuteStr: String = if(minute < 10) "0$minute" else "$minute"

    return "$hourStr:$minuteStr"
}
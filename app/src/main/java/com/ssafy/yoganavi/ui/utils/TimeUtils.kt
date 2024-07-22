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

fun formatDashWeekDate(milliseconds: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault())
    val date = Date(milliseconds)
    val tempStr = formatter.format(date)
    return tempStr.substring(0,tempStr.length-2)
}

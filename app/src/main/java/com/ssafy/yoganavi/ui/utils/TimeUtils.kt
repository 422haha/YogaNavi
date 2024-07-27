package com.ssafy.yoganavi.ui.utils

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(milliseconds: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(milliseconds  * 1000)
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

fun formatZeroDate(hour: Int, minute: Int): String {
    val hourStr: String = if(hour < 10) "0$hour" else "$hour"

    val minuteStr: String = if(minute < 10) "0$minute" else "$minute"

    return "$hourStr:$minuteStr"
}

fun convertDaysToHangle(days: String): String {
    val dayList = days.split(",")
    val hangleDays = dayList.map { day ->
        try { Week.valueOf(day).hangle }
        finally { "" }
    }

    return hangleDays.joinToString(", ")
}

fun IntToDate(year: Int, month: Int, day: Int): String = run { "$year.${month + 1}.$day" }

fun StartVerticalEnd(start: String, end: String): String = run { "$start | $end" }

fun StartTildeEnd(start: String, end: String) = "$start~$end"
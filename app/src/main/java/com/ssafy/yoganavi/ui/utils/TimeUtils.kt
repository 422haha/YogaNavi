package com.ssafy.yoganavi.ui.utils

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.time.Duration
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTime(milliseconds: Long): String {
    val hours = (milliseconds / (3600 * 1000)).toInt() // 시간 계산
    val minutes = ((milliseconds % (3600 * 1000)) / (60 * 1000)).toInt() // 분 계산
    return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
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
    return tempStr.substring(0, tempStr.length - 2)
}

fun formatZeroDate(hour: Int, minute: Int): String {
    val hourStr: String = if(hour < 10) "0$hour" else "$hour"

    val minuteStr: String = if(minute < 10) "0$minute" else "$minute"

    return "$hourStr:$minuteStr"
}
// CalendarDay를 Long으로 변환하는 확장 함수
fun CalendarDay.toLong(flag:Int): Long {
    val calendar = Calendar.getInstance()
    if(flag== START){
        calendar.set(this.year, this.month - 1, this.day,0,0,0)
    }
    else{//END
        calendar.set(this.year, this.month - 1, this.day,23,59,59)
    }
    return calendar.timeInMillis
}

// Long 값을 CalendarDay로 변환하는 확장 함수
fun Long.toCalendarDay(): CalendarDay {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}
fun convertDaysToHangle(days: String): String {
    val dayList = days.split(",")
    val hangleDays = dayList.map { day ->
        try { Week.valueOf(day).hangle }
        finally { "" }
    }

    return hangleDays.joinToString(", ")
}

fun intToDate(year: Int, month: Int, day: Int): String = run { "$year.${month + 1}.$day" }

fun startVerticalEnd(start: String, end: String): String = run { "$start | $end" }

fun startTildeEnd(start: String, end: String) = "$start~$end"

fun startSpaceEnd(start: String, end: String) = "$start $end"

fun addYear(currentMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = currentMillis
    calendar.add(Calendar.YEAR, 1)

    return calendar.timeInMillis
}

fun parseDay(englishDays: String): String {
    val dayMapping = mapOf(
        "MON" to "월",
        "TUE" to "화",
        "WED" to "수",
        "THU" to "목",
        "FRI" to "금",
        "SAT" to "토",
        "SUN" to "일"
    )

    val englishDayList = englishDays.split(",")
    val koreanDayList = englishDayList.map { dayMapping[it] ?: it }

    return koreanDayList.joinToString(",")
}

fun Long.msToDuration(): String {
    val minutes = (this / 1000) / 60
    val seconds = (this / 1000) % 60
    return String.format(Locale.KOREA, "%d:%02d", minutes, seconds)
}

@RequiresApi(Build.VERSION_CODES.O)
fun subTime() {
    val startTime = LocalTime.of(23, 0) // 23:00
    val endTime = LocalTime.of(2, 0) // 02:00

    val duration = if (endTime.isBefore(startTime)) {
        Duration.between(startTime, endTime.plusHours(24)) // 자정을 넘긴 시간 계산
    } else {
        Duration.between(startTime, endTime)
    }

    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
}
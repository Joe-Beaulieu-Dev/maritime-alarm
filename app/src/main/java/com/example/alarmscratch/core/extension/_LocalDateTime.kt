package com.example.alarmscratch.core.extension

import android.content.Context
import com.example.alarmscratch.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

object LocalDateTimeUtil {
    fun nowTruncated(): LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)
}

fun LocalDateTime.futurizeDateTime(): LocalDateTime {
    val currentDateTime = LocalDateTimeUtil.nowTruncated()
    return if (!this.isAfter(currentDateTime)) {
        val potentialAlarm = LocalDateTime.of(currentDateTime.toLocalDate(), this.toLocalTime())

        // Add the minimum amount of days required to futurize the Alarm
        if (!potentialAlarm.isAfter(currentDateTime)) {
            potentialAlarm.plusDays(1)
        } else {
            potentialAlarm
        }
    } else {
        this
    }
}

fun LocalDateTime.toAlarmDateString(context: Context) : String {
    val currentDateTime = LocalDateTimeUtil.nowTruncated()
    return if (!this.isBefore(currentDateTime)) {
        val alarmDate = this.toLocalDate()
        val currentDate = currentDateTime.toLocalDate()

        // Alarm is for today
        if (alarmDate.isEqual(currentDate)) {
            context.getString(R.string.date_today)
        } else if (alarmDate.dayOfYear - currentDate.dayOfYear == 1) { // Alarm is for tomorrow
            context.getString(R.string.date_tomorrow)
        } else { // Alarm is for a day beyond tomorrow
            formatCalendarDate(alarmDate)
        }
    } else {
        context.getString(R.string.error)
    }
}

// TODO do something different with Locale
private fun formatCalendarDate(date: LocalDate): String =
    "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)}, " +
            "${date.month.getDisplayName(TextStyle.SHORT, Locale.US)} " +
            "${date.dayOfMonth.toOrdinal()} " +
            "${date.year}"

fun LocalDateTime.toNotificationDateTimeString(context: Context): String =
    "${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)}, ${get12HrTime()} ${getAmPm(context)}"

fun LocalDateTime.get12HrTime(): String {
    val time = this.toLocalTime()
    val minute = if (time.minute < 10) {
        "0${time.minute}"
    } else {
        "${time.minute}"
    }

    return if (time.hour == 0) { // Midnight
        "12:$minute"
    } else if (time.hour <= 12) {
        "${time.hour}:$minute"
    } else {
        "${time.hour - 12}:$minute"
    }
}

fun LocalDateTime.getAmPm(context: Context): String =
    if (this.toLocalTime().hour < 12) {
        context.getString(R.string.time_am)
    } else {
        context.getString(R.string.time_pm)
    }

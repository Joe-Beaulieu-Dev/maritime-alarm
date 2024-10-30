package com.example.alarmscratch.core.extension

import android.content.Context
import com.example.alarmscratch.R
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object LocalDateTimeUtil {
    fun nowTruncated(): LocalDateTime =
        LocalDateTime.now().withSecond(0).withNano(0)

    fun defaultSnoozeDateTime(): LocalDateTime =
        nowTruncated().plusMinutes(AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION.toLong())
}

/*
 * Utility
 */

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

/*
 * Convenience
 */

fun LocalDateTime.zonedEpochMillis(): Long =
    atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

fun LocalDateTime.getDayFull(): String =
    dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)

fun LocalDateTime.getDayShorthand(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

/*
 * Formatting
 */

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

fun LocalDateTime.to12HourNotificationDateTimeString(context: Context): String =
    "${getDayShorthand()}, ${get12HourTime()} ${getAmPm(context)}"

fun LocalDateTime.to24HourNotificationDateTimeString(): String =
    "${getDayShorthand()}, ${get24HourTime()}"

fun LocalDateTime.get12HourTime(): String {
    val time = this.toLocalTime()
    val minute = getFormattedMinute(time)

    return if (time.hour == 0) { // Midnight
        "12:$minute"
    } else if (time.hour <= 12) {
        "${time.hour}:$minute"
    } else {
        "${time.hour - 12}:$minute"
    }
}

fun LocalDateTime.get24HourTime(): String {
    val time = this.toLocalTime()

    return if (time.hour < 10) {
        "0${time.hour}:${getFormattedMinute(time)}"
    } else {
        "${time.hour}:${getFormattedMinute(time)}"
    }
}

fun LocalDateTime.getAmPm(context: Context): String =
    if (this.toLocalTime().hour < 12) {
        context.getString(R.string.time_am)
    } else {
        context.getString(R.string.time_pm)
    }

private fun getFormattedMinute(time: LocalTime): String =
    if (time.minute < 10) {
        "0${time.minute}"
    } else {
        "${time.minute}"
    }

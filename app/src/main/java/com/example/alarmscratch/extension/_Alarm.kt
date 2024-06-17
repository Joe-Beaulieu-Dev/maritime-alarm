package com.example.alarmscratch.extension

import com.example.alarmscratch.data.model.Alarm

fun Alarm.isRepeating(): Boolean = weeklyRepeater.hasRepeatingDays()

fun Alarm.get12HrTime(): String {
    val time = dateTime.toLocalTime()
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

package com.example.alarmscratch.ui.alarmlist.preview

import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.model.WeeklyRepeater
import java.time.LocalDateTime

const val tueWedThu: Int = 28

// TODO do exception handling for java code
// YYYY-MM-DDTHH:MM:SS
val alarmSampleData: List<Alarm> = listOf(
    // Repeating
    Alarm(
        name = "Wake up",
        enabled = true,
        // TODO: Make this set the Date according to the next time the
        //  Alarm is going to go off based on the WeeklyRepeater
        dateTime = getTomorrowAtTime24Hr(hour = 8, minute = 30, second = 0),
        weeklyRepeater = WeeklyRepeater(encodedRepeatingDays = tueWedThu)
    ),
    // Today
    Alarm(
        name = "Eat pizza",
        enabled = true,
        dateTime = getTodayAtTime24Hr(hour = 23, minute = 59, second = 0),
        weeklyRepeater = WeeklyRepeater()
    ),
    // Tomorrow
    Alarm(
        name = "Do a flip",
        enabled = false,
        dateTime = getTomorrowAtTime24Hr(hour = 14, minute = 30, second = 0),
        weeklyRepeater = WeeklyRepeater()
    ),
    // Calendar day
    Alarm(
        name = "",
        enabled = true,
        dateTime = LocalDateTime.parse("2024-12-25T13:05:00"),
        weeklyRepeater = WeeklyRepeater()
    )
)

/**
 * Alarm List with hard coded IDs for use in LazyColumn
 */
val alarmSampleDataHardCodedIds: List<Alarm> = alarmSampleData.mapIndexed { index, alarm -> alarm.copy(id = index) }

// TODO do exception handling for java code
val consistentFutureAlarm: Alarm =
    Alarm(
        name = "",
        enabled = true,
        dateTime = LocalDateTime.now().withNano(0).plusHours(8).plusMinutes(45),
        weeklyRepeater = WeeklyRepeater()
    )

// TODO do exception handling for java code
private fun getTodayAtTime24Hr(hour: Int, minute: Int, second: Int): LocalDateTime =
    LocalDateTime.now().withNano(0)
        .withHour(hour)
        .withMinute(minute)
        .withSecond(second)

// TODO do exception handling for java code
private fun getTomorrowAtTime24Hr(hour: Int, minute: Int, second: Int): LocalDateTime =
    LocalDateTime.now().plusDays(1).withNano(0)
        .withHour(hour)
        .withMinute(minute)
        .withSecond(second)

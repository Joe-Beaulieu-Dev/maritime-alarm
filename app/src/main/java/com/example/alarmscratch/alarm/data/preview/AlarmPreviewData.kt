package com.example.alarmscratch.alarm.data.preview

import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.data.model.RingtoneData
import java.time.LocalDateTime

const val tueWedThu: Int = 28
private const val sampleRingtoneUriString = "content://settings/system/alarm_alert"

val repeatingAlarm =
    Alarm(
        name = "Wake up",
        enabled = true,
        // TODO: Make this set the Date according to the next time the
        //  Alarm is going to go off based on the WeeklyRepeater
        dateTime = getTomorrowAtTime24Hr(hour = 8, minute = 30, second = 0),
        weeklyRepeater = WeeklyRepeater(encodedRepeatingDays = tueWedThu),
        ringtoneUriString = sampleRingtoneUriString
    )

val todayAlarm =
    Alarm(
        name = "Eat pizza",
        enabled = true,
        dateTime = getTodayAtTime24Hr(hour = 23, minute = 59, second = 0),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString
    )

val tomorrowAlarm =
    Alarm(
        name = "Do a flip",
        enabled = false,
        dateTime = getTomorrowAtTime24Hr(hour = 14, minute = 0, second = 0),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString
    )

// TODO do exception handling for java code
val calendarAlarm =
    Alarm(
        name = "",
        enabled = true,
        dateTime = LocalDateTime.parse("2024-12-25T00:05:00"),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString
    )

// TODO do exception handling for java code
val consistentFutureAlarm: Alarm =
    Alarm(
        name = "",
        enabled = true,
        dateTime = LocalDateTime.now().withNano(0).plusHours(8).plusMinutes(45),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString
    )

// YYYY-MM-DDTHH:MM:SS
val alarmSampleData: List<Alarm> = listOf(repeatingAlarm, todayAlarm, tomorrowAlarm, calendarAlarm)

/**
 * Alarm List with hard coded IDs for use in Previews
 */
val alarmSampleDataHardCodedIds: List<Alarm> = alarmSampleData.mapIndexed { index, alarm -> alarm.copy(id = index) }

val sampleRingtoneData = RingtoneData(id = 0, name = "Ringtone Name", baseUri = sampleRingtoneUriString)

val ringtoneDataSampleList: List<RingtoneData> = listOf(
    sampleRingtoneData,
    RingtoneData(id = 1, name = "Ringtone 1", baseUri = "ringtone1BaseUri"),
    RingtoneData(id = 2, name = "Ringtone 2", baseUri = "ringtone2BaseUri"),
    RingtoneData(id = 3, name = "Ringtone 3", baseUri = "ringtone3BaseUri"),
    RingtoneData(id = 4, name = "Ringtone 4", baseUri = "ringtone4BaseUri")
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

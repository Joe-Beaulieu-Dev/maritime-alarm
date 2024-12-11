package com.example.alarmscratch.alarm.data.preview

import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import java.time.LocalDateTime

const val tueWedThu: Int = 28
const val everyDay: Int = 127
private const val sampleRingtoneUriString = "content://settings/system/alarm_alert"

val repeatingAlarm =
    Alarm(
        name = "Wake up",
        enabled = true,
        // TODO: Make this set the Date according to the next time the
        //  Alarm is going to go off based on the WeeklyRepeater
        dateTime = getTomorrowAtTime24Hr(hour = 8, minute = 30),
        weeklyRepeater = WeeklyRepeater(encodedRepeatingDays = tueWedThu),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = true,
        snoozeDuration = 5
    )

val todayAlarm =
    Alarm(
        name = "Eat pizza",
        enabled = true,
        dateTime = getTodayAtTime24Hr(hour = 23, minute = 59),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = false,
        snoozeDuration = 10
    )

val tomorrowAlarm =
    Alarm(
        name = "Do a flip",
        enabled = false,
        dateTime = getTomorrowAtTime24Hr(hour = 14, minute = 0),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = true,
        snoozeDuration = 15
    )

val calendarAlarm =
    Alarm(
        name = "",
        enabled = true,
        dateTime = LocalDateTimeUtil.nowTruncated().plusDays(2),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = false,
        snoozeDuration = 20
    )

val consistentFutureAlarm =
    Alarm(
        name = "Practice",
        enabled = true,
        dateTime = getFutureTime(plusHours = 8, plusMinutes = 45),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = true,
        snoozeDuration = 25
    )

val snoozedAlarm =
    Alarm(
        name = "Gym",
        enabled = true,
        dateTime = LocalDateTimeUtil.nowTruncated(),
        weeklyRepeater = WeeklyRepeater(),
        ringtoneUriString = sampleRingtoneUriString,
        isVibrationEnabled = true,
        snoozeDateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(25),
        snoozeDuration = 25
    )

// YYYY-MM-DDTHH:MM:SS
val alarmSampleData: List<Alarm> = listOf(repeatingAlarm, todayAlarm, tomorrowAlarm, calendarAlarm)

/**
 * Alarm List with hard coded IDs for use in Previews
 */
val alarmSampleDataHardCodedIds: List<Alarm> = alarmSampleData.mapIndexed { index, alarm -> alarm.copy(id = index) }

val sampleRingtoneData = RingtoneData(id = 0, name = "Ringtone 1", baseUri = sampleRingtoneUriString)

val ringtoneDataSampleList: List<RingtoneData> = listOf(
    sampleRingtoneData,
    RingtoneData(id = 1, name = "Ringtone 2", baseUri = "ringtone2BaseUri"),
    RingtoneData(id = 2, name = "Ringtone 3", baseUri = "ringtone3BaseUri"),
    RingtoneData(id = 3, name = "Ringtone 4", baseUri = "ringtone4BaseUri"),
    RingtoneData(id = 4, name = "Ringtone 5", baseUri = "ringtone5BaseUri")
)

// TODO do exception handling for java code
private fun getTodayAtTime24Hr(hour: Int, minute: Int): LocalDateTime =
    LocalDateTimeUtil.nowTruncated().withHour(hour).withMinute(minute)

// TODO do exception handling for java code
private fun getTomorrowAtTime24Hr(hour: Int, minute: Int): LocalDateTime =
    getTodayAtTime24Hr(hour, minute).plusDays(1)

private fun getFutureTime(
    plusDays: Long = 0,
    plusHours: Long = 0,
    plusMinutes: Long = 0
): LocalDateTime =
    LocalDateTimeUtil.nowTruncated()
        .plusDays(plusDays)
        .plusHours(plusHours)
        .plusMinutes(plusMinutes)

package com.example.alarmscratch.core.extension

import android.content.Context
import android.media.Ringtone
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.util.AlarmUtil
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor

/*
 * Utility
 */

fun Alarm.toAlarmExecutionData(): AlarmExecutionData =
    AlarmExecutionData(
        id = id,
        name = name,
        executionDateTime = snoozeDateTime ?: dateTime,
        repeatingDays = weeklyRepeater.toEncodedRepeatingDays(),
        ringtoneUri = ringtoneUriString,
        isVibrationEnabled = isVibrationEnabled,
        snoozeDuration = snoozeDuration
    )

fun Alarm.withFuturizedDateTime(): Alarm {
    val currentDateTime = LocalDateTimeUtil.nowTruncated()

    val futurizedDateTime = if (!dateTime.isAfter(currentDateTime)) {
        if (isRepeating()) {
            AlarmUtil.nextRepeatingDateTime(dateTime, weeklyRepeater)
        } else {
            val potentialAlarm = LocalDateTime.of(currentDateTime.toLocalDate(), dateTime.toLocalTime())
            // Add the minimum amount of days required to futurize the Alarm
            if (!potentialAlarm.isAfter(currentDateTime)) {
                potentialAlarm.plusDays(1)
            } else {
                potentialAlarm
            }
        }
    } else {
        dateTime
    }

    return this.copy(dateTime = futurizedDateTime)
}

/*
 * Convenience
 */

fun Alarm.isRepeating(): Boolean =
    weeklyRepeater.hasRepeatingDays()

fun Alarm.isSnoozed(): Boolean =
    snoozeDateTime != null

/**
 * Returns whether or not the Alarm is dirty. Dirty Alarms are those that have invalid configurations.
 * This can happen if the phone is off during a time in which an Alarm is scheduled to execute.
 *
 * Returns true if, and only if, both of the following conditions are met:
 * 1) Alarm is enabled
 * 2) Alarm is not configured to go off in the future, taking snooze into account
 *
 * @return true if the Alarm is dirty, false otherwise
 */
fun Alarm.isDirty(): Boolean {
    val now = LocalDateTimeUtil.nowTruncated()
    return enabled &&
            if (isSnoozed()) {
                snoozeDateTime?.isAfter(now) == false
            } else {
                !dateTime.isAfter(now)
            }
}

fun Alarm.getRingtone(context: Context): Ringtone =
    RingtoneRepository(context).getRingtone(ringtoneUriString)

/*
 * Formatting
 */

fun Alarm.toCountdownString(context: Context): String {
    // Don't truncate the current time since the seconds are used here
    val now = LocalDateTime.now()
    val alarmExecutionDateTime = snoozeDateTime ?: dateTime

    // If this method is called with a LocalDateTime that is not in the future,
    // just return "0m" by default. This method is not set up to calculate for the past.
    if (!alarmExecutionDateTime.isAfter(now)) {
        return "0${context.getString(R.string.minute_abbreviation)}"
    }

    val secondsTillNextAlarm = now.until(alarmExecutionDateTime, ChronoUnit.SECONDS).toDouble()

    // Days
    var days = floor(secondsTillNextAlarm / 86400)
    val remainderAfterDays = secondsTillNextAlarm - days * 86400
    // Hours
    var hours = floor(remainderAfterDays / 3600)
    val remainderAfterHours = remainderAfterDays - hours * 3600
    // Minutes - round the minutes since we're not displaying seconds
    val minutesRaw = remainderAfterHours / 60
    val minutes =
        if (minutesRaw <= 59) {
            ceil(minutesRaw)
        } else {
            // Special Case:
            // 59.X minutes where X > 0
            //   - Round minutes down to 0, and add 1 to hour to avoid displaying "60m"
            //   - Ex: We don't want to round "1hr 59.5m" to "1hr 60m", instead we want "2h"
            //   - NOTE: If hours is 23, then we should continue this same logic upwards because
            //     we also do not want to round "1d 23h 59.5m" "1d 24h", instead we want "2d".
            if (hours >= 23) {
                days += 1
                hours = 0.0
            } else {
                hours += 1
            }

            // Set minutes to 0
            0.0
        }

    // Adding spaces between sections can get a bit messy since you won't always have every
    // section present. Create the String without spaces first, then add the spaces afterwards.
    val stringBuilder = StringBuilder().apply {
        if (days >= 1) append("${days.toInt()}${context.getString(R.string.day_abbreviation)}")
        if (hours >= 1) append("${hours.toInt()}${context.getString(R.string.hour_abbreviation)}")
        if (minutes >= 1) {
            // Add "<" if there's only one minute left
            if (minutes == 1.0 && hours == 0.0 && days == 0.0) {
                append("${context.getString(R.string.less_than_symbol)} ")
            }
            append("${minutes.toInt()}${context.getString(R.string.minute_abbreviation)}")
        }
    }

    // Add spaces between each section
    stringBuilder.forEachIndexed { index, c ->
        if (c.isLetter() && index < stringBuilder.lastIndex) {
            stringBuilder.insert(index + 1, " ")
        }
    }

    return stringBuilder.toString()
}

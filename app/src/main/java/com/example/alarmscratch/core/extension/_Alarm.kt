package com.example.alarmscratch.core.extension

import android.content.Context
import android.media.Ringtone
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import java.time.DayOfWeek
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
        ringtoneUri = ringtoneUriString,
        isVibrationEnabled = isVibrationEnabled,
        snoozeDuration = snoozeDuration
    )

fun Alarm.withFuturizedDateTime(): Alarm {
    val currentDateTime = LocalDateTimeUtil.nowTruncated()

    val futurizedDateTime = if (!dateTime.isAfter(currentDateTime)) {
        if (weeklyRepeater.hasRepeatingDays()) {
            nextRepeatingDateTime()
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

/**
 * Returns a LocalDateTime with the next day the Alarm is set to go off, if and only if the Alarm is set to repeat.
 * The returned LocalDateTime is guaranteed to be set in the future.
 *
 * If the Alarm is only set to repeat on days that would result in this function returning a LocalDateTime in
 * the past, then this function will add one week to the chosen day. See examples below.
 *
 * Ex 1: It's currently Wednesday, 7/17/2024 at 5:30pm. The Alarm is set to go off at 8:30am and it is only set to repeat on Wednesday.
 *       This function would return Wednesday, 7/24/2024 at 8:30am.
 *
 * Ex 2: It's currently Wednesday, 7/17/2024 at 5:30pm. The Alarm is set to go off at 8:30am and it is only set to repeat on Tuesday.
 *       This function would return Tuesday, 7/23/2024 at 8:30am.
 */
fun Alarm.nextRepeatingDateTime(): LocalDateTime {
    if (isRepeating()) {
        val currentDateTime = LocalDateTimeUtil.nowTruncated()
        val currentDate = currentDateTime.toLocalDate()
        val currentDay = currentDate.dayOfWeek
        val sortedRepeatingDays = sortRepeatingDaysByPreference(currentDay, weeklyRepeater.getRepeatingDays())

        // If setting the Alarm to Today with the current Alarm Time would result in an Alarm
        // that's not set in the future, then:
        //
        // If there are NOT other days in the array -> return a LocalDateTime that is set one week from Today
        // If there are other days in the array     -> remove Today from the list
        var daysBetween = sortedRepeatingDays[0].dayNumber() - currentDay.dayNumber()
        if (daysBetween == 0) {
            val potentialAlarm = LocalDateTime.of(currentDate, dateTime.toLocalTime())
            if (!potentialAlarm.isAfter(currentDateTime)) {
                if (sortedRepeatingDays.size == 1) {
                    return LocalDateTime.of(currentDate.plusDays(7), dateTime.toLocalTime())
                } else {
                    daysBetween = sortedRepeatingDays[1].dayNumber() - currentDay.dayNumber()
                }
            }
        }

        val offsetDays = if (daysBetween < 0) {
            daysBetween + 7
        } else {
            daysBetween
        }

        return LocalDateTime.of(currentDate.plusDays(offsetDays.toLong()), dateTime.toLocalTime())
    } else {
        return dateTime
    }
}

/**
 * Sorts a list of [WeeklyRepeater.Day], in relation to [today], given the below precedence.
 * Note: Each subset listed below will be sorted in ascending order
 *
 * First: today
 * Second: days after today
 * Third: days before today
 *
 * @param today DayOfWeek corresponding to today
 * @param repeatingDays a List of WeeklyRepeater.Day, pre-sorted in standard week order, where the week starts with Sunday
 *
 * @return a List of WeeklyRepeater.Day sorted according to the above precedent
 */
private fun sortRepeatingDaysByPreference(
    today: DayOfWeek,
    repeatingDays: List<WeeklyRepeater.Day>
): List<WeeklyRepeater.Day> =
    repeatingDays
        .filter { it.dayNumber() >= today.dayNumber() }
        .plus(repeatingDays.filter { it.dayNumber() < today.dayNumber() })

/*
 * Convenience
 */

fun Alarm.isRepeating(): Boolean =
    weeklyRepeater.hasRepeatingDays()

fun Alarm.isSnoozed(): Boolean =
    snoozeDateTime != null

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

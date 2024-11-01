package com.example.alarmscratch.core.extension

import android.content.Context
import android.media.Ringtone
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.data.repository.RingtoneRepository
import java.time.DayOfWeek
import java.time.LocalDateTime

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
fun Alarm.nextRepeatingDate(): LocalDateTime {
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

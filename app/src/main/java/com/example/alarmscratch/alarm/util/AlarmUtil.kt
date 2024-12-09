package com.example.alarmscratch.alarm.util

import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.dayNumber
import java.time.DayOfWeek
import java.time.LocalDateTime

object AlarmUtil {

    /**
     * Returns a LocalDateTime with the next day the Alarm is set to go off, if and only if the Alarm is set to repeat.
     * The returned LocalDateTime is guaranteed to be set in the future if you passed data from a repeating Alarm.
     * However, if the Alarm is not set to repeat, then the LocalDateTime passed to this function will be returned, unmodified.
     *
     * If the Alarm is repeating, and is only set to repeat on days that would result in this function returning a LocalDateTime in
     * the past, then this function will add one week to the chosen day. See examples below.
     *
     * Ex 1: It's currently Wednesday, 7/17/2024 at 5:30pm. The Alarm is set to go off at 8:30am and it is only set to repeat on Wednesday.
     *       This function would return Wednesday, 7/24/2024 at 8:30am.
     *
     * Ex 2: It's currently Wednesday, 7/17/2024 at 5:30pm. The Alarm is set to go off at 8:30am and it is only set to repeat on Tuesday.
     *       This function would return Tuesday, 7/23/2024 at 8:30am.
     *
     * @param alarmDateTime the Alarm's current LocalDateTime, unsnoozed. Do not pass a snoozed LocalDateTime.
     * @param weeklyRepeater current day of week repeater configuration
     *
     * @return the next repeating LocalDateTime if the WeeklyRepeater has repeating days.
     *         Otherwise, this will return the LocalDateTime passed in, unmodified.
     */
    fun nextRepeatingDateTime(alarmDateTime: LocalDateTime, weeklyRepeater: WeeklyRepeater): LocalDateTime {
        if (weeklyRepeater.hasRepeatingDays()) {
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
                val potentialAlarm = LocalDateTime.of(currentDate, alarmDateTime.toLocalTime())
                if (!potentialAlarm.isAfter(currentDateTime)) {
                    if (sortedRepeatingDays.size == 1) {
                        return LocalDateTime.of(currentDate.plusDays(7), alarmDateTime.toLocalTime())
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

            return LocalDateTime.of(currentDate.plusDays(offsetDays.toLong()), alarmDateTime.toLocalTime())
        } else {
            return alarmDateTime
        }
    }

    /**
     * Sorts a List of [WeeklyRepeater.Day], in relation to [today], given the below precedence.
     * The List passed to this method **MUST** already be sorted in standard week order,
     * where the week starts with Sunday.
     *
     * Note: Each subset listed below will also be sorted in standard week order in the
     * output List, where the week starts with Sunday.
     *
     * Output List subsets, in order:
     * - First: today
     * - Second: days after today
     * - Third: days before today
     *
     * Ex:
     * - Today is Thursday
     * - Input: Sunday, Tuesday, Thursday, Friday, Saturday
     * - Output: Thursday, Friday, Saturday, Sunday, Tuesday
     *
     * @param today DayOfWeek corresponding to today
     * @param repeatingDays a List of WeeklyRepeater.Day, pre-sorted in standard week order, where the week starts with Sunday
     *
     * @return a List of WeeklyRepeater.Day sorted according to the above precedence
     */
    private fun sortRepeatingDaysByPreference(
        today: DayOfWeek,
        repeatingDays: List<WeeklyRepeater.Day>
    ): List<WeeklyRepeater.Day> =
        repeatingDays
            .filter { it.dayNumber() >= today.dayNumber() }
            .plus(repeatingDays.filter { it.dayNumber() < today.dayNumber() })
}

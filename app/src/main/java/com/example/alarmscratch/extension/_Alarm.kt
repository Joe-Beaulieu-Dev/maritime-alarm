package com.example.alarmscratch.extension

import com.example.alarmscratch.data.model.Alarm
import java.time.LocalDateTime

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
fun Alarm.nextRepeatingDate(): LocalDateTime? {
    if (weeklyRepeater.hasRepeatingDays()) {
        val repeatingDays = weeklyRepeater.getRepeatingDays()
        val currentDateTime = LocalDateTimeUtil.nowTruncated()
        val currentDate = currentDateTime.toLocalDate()
        val currentDay = currentDate.dayOfWeek.toWeeklyRepeaterDay()
        var offsetDays = 0

        currentDay?.let { today ->
            val daysGreaterThanOrEqualToToday = repeatingDays.filter { it.dayNumber() >= today.dayNumber() }
            val daysLessThanToday = repeatingDays.filter { it.dayNumber() < today.dayNumber() }
            var daysSortedByPreference = daysGreaterThanOrEqualToToday.plus(daysLessThanToday)

            var daysBetween = daysSortedByPreference[0].dayNumber() - currentDay.dayNumber()

            // Handle potential special case where the first potential repeating day is Today
            if (daysBetween == 0) {
                val potentialAlarm = LocalDateTime.of(currentDate, this.dateTime.toLocalTime())
                // If setting the Alarm to Today with the current Alarm Time would result in an Alarm
                // that's either set in the past, or the current time:
                //
                // If there are NOT other days in the array -> return a LocalDateTime that is set one week from Today
                // If there are other days in the array     -> remove Today from the list
                if (!potentialAlarm.isAfter(currentDateTime)) {
                    if (daysSortedByPreference.size == 1) {
                        return LocalDateTime.of(currentDate.plusDays(7), this.dateTime.toLocalTime())
                    } else {
                        daysSortedByPreference = daysSortedByPreference.drop(1)
                    }
                }
            }

            // Re-calculate in case the first day was removed from the List while handling the special case above
            daysBetween = daysSortedByPreference[0].dayNumber() - currentDay.dayNumber()

            offsetDays = if (daysBetween < 0) {
                daysBetween + 7
            } else {
                daysBetween
            }

            return LocalDateTime.of(currentDate.plusDays(offsetDays.toLong()), this.dateTime.toLocalTime())
        }
        return null
    } else {
        return null
    }
}

package com.joebsource.lavalarm.alarm.data.model

import android.content.Context
import com.joebsource.lavalarm.R
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor

sealed interface AlarmStringFormatter {

    data object Countdown : AlarmStringFormatter {

        /**
         * Returns a String in the following format: Xd Yh Zm
         *
         * @param context Context used to get String Resources
         *
         * @return a String in the following format: Xd Yh Zm
         */
        override fun format(context: Context, alarm: Alarm): String =
            DaysHoursMinutes.fromAlarm(alarm).toString(context, " ")
    }

    data object ScheduleConfirmation : AlarmStringFormatter {

        /**
         * Returns a String in the following format: Alarm scheduled for Xd, Yh, Zm from now
         *
         * @param context Context used to get String Resources
         *
         * @return a String in the following format: Alarm scheduled for Xd, Yh, Zm from now
         */
        override fun format(context: Context, alarm: Alarm): String =
            context.getString(
                R.string.snackbar_alarm_scheduled,
                DaysHoursMinutes.fromAlarm(alarm).toString(context, ", ")
            )
    }

    fun format(context: Context, alarm: Alarm): String
}

// I'd prefer for this to be in a separate file as package-private, but package-private does not
// exist in Kotlin so I'm just making it private and keeping it here.
private class DaysHoursMinutes(
    private val days: Int = 0,
    private val hours: Int = 0,
    private val minutes: Int = 0
) {

    companion object {

        fun fromAlarm(alarm: Alarm): DaysHoursMinutes {
            // Don't truncate the current time since the seconds are used here
            val now = LocalDateTime.now()
            val alarmExecutionDateTime = alarm.snoozeDateTime ?: alarm.dateTime

            // If this method is called with a LocalDateTime that is not in the future
            // then just return the default. This method is not set up to calculate for the past.
            if (!alarmExecutionDateTime.isAfter(now)) {
                return DaysHoursMinutes()
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

            return DaysHoursMinutes(days.toInt(), hours.toInt(), minutes.toInt())
        }
    }

    /**
     * Returns a String which represents the hours, minutes, and seconds of this Object separated by a given delimiter.
     * The output String will be in the following format, using the following variables:
     *
     * Variables
     * 1) X, Y, and Z are arbitrary integers
     * 2) d, h, and y represent days, hours, and minutes, respectively
     * 3) {D} is a given delimiter
     *
     * Format
     * - Xd{D}Yh{D}Zm
     *
     * Ex: 1d, 2h, 35m
     *
     * @param context Context used to get String Resources
     * @param delimiter delimiter to be placed between the hours, minutes, and seconds of the output String
     *
     * @return a String in the following format: Xd{D}Yh{D}Zm
     */
    fun toString(context: Context, delimiter: String): String {
        // Special case when there's no time. Return "0m".
        if (days == 0 && hours == 0 && minutes == 0) {
            return "0${context.getString(R.string.minute_abbreviation)}"
        }

        // Adding spaces between sections can get a bit messy since you won't always have every
        // section present. Create the String without spaces first, then add the spaces afterwards.
        val stringBuilder = StringBuilder().apply {
            if (days >= 1) append("$days${context.getString(R.string.day_abbreviation)}")
            if (hours >= 1) append("$hours${context.getString(R.string.hour_abbreviation)}")
            if (minutes >= 1) {
                // Add "<" if there's only one minute left
                if (minutes == 1 && hours == 0 && days == 0) {
                    append("${context.getString(R.string.less_than_symbol)} ")
                }
                append("$minutes${context.getString(R.string.minute_abbreviation)}")
            }
        }

        // Add spaces between each section
        stringBuilder.forEachIndexed { index, c ->
            if (c.isLetter() && index < stringBuilder.lastIndex) {
                stringBuilder.insert(index + 1, delimiter)
            }
        }

        return stringBuilder.toString()
    }
}

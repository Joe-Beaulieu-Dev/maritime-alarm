package com.joebsource.lavalarm.core.extension

import android.content.Context
import android.media.Ringtone
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.AlarmExecutionData
import com.joebsource.lavalarm.alarm.data.model.AlarmStringFormatter
import com.joebsource.lavalarm.alarm.util.AlarmUtil
import com.joebsource.lavalarm.core.data.repository.RingtoneRepository
import java.time.LocalDateTime

/*
 * Utility
 */

fun Alarm.toAlarmExecutionData(): AlarmExecutionData =
    AlarmExecutionData(
        id = id,
        name = name,
        executionDateTime = snoozeDateTime ?: dateTime,
        encodedRepeatingDays = weeklyRepeater.toEncodedRepeatingDays(),
        ringtoneUri = ringtoneUri,
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
    RingtoneRepository(context).getRingtone(ringtoneUri)

/*
 * Formatting
 */

/**
 * Returns a String in the following format: Xd Yh Zm
 *
 * @param context Context used to get String Resources
 *
 * @return a String in the following format: Xd Yh Zm
 */
fun Alarm.toCountdownString(context: Context): String =
    AlarmStringFormatter.Countdown.format(context, this)

/**
 * Returns a String in the following format: Alarm scheduled for Xd, Yh, Zm from now
 *
 * @param context Context used to get String Resources
 *
 * @return a String in the following format: Alarm scheduled for Xd, Yh, Zm from now
 */
fun Alarm.toScheduleString(context: Context): String =
    AlarmStringFormatter.ScheduleConfirmation.format(context, this)

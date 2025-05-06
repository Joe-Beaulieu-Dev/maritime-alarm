package com.octrobi.lavalarm.alarm.alarmexecution

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.AlarmExecutionData
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.alarm.util.AlarmUtil
import com.octrobi.lavalarm.core.MainActivity
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.isDirty
import com.octrobi.lavalarm.core.extension.isRepeating
import com.octrobi.lavalarm.core.extension.isSnoozed
import com.octrobi.lavalarm.core.extension.toAlarmExecutionData
import com.octrobi.lavalarm.core.extension.zonedEpochMillis

object AlarmScheduler {

    /*
     * Scheduling
     */

    fun scheduleAlarm(context: Context, alarmExecutionData: AlarmExecutionData) {
        // Create AlarmClockInfo that launches the application
        val alarmClockInfo = AlarmClockInfo(
            alarmExecutionData.executionDateTime.zonedEpochMillis(),
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        // Create PendingIntent to execute Alarm
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmExecutionData.id,
            AlarmIntentBuilder.executeAlarmIntent(context, alarmExecutionData),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule Alarm
        getAlarmManager(context).setAlarmClock(
            alarmClockInfo,
            alarmPendingIntent
        )
    }

    fun cancelAlarm(context: Context, alarmExecutionData: AlarmExecutionData) {
        // Create PendingIntent to cancel Alarm.
        // This Intent is never executed. It is only used by the AlarmManager for Alarm cancellation.
        val alarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // This action needs to be the same as the scheduling action.
            // This is because the Intent must pass Intent.filterEquals(), which looks at the action among other things,
            // in order for the AlarmManager to be able to find the Alarm with the "same Intent" to cancel.
            action = AlarmActionReceiver.ACTION_EXECUTE_ALARM
        }

        // Cancel Alarm
        getAlarmManager(context).cancel(
            PendingIntent.getBroadcast(
                context,
                alarmExecutionData.id,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    /*
     * Rescheduling
     */

    suspend fun snoozeAndRescheduleAlarm(context: Context, alarmRepository: AlarmRepository, alarm: Alarm) {
        // Update Alarm in database
        val snoozeDateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(alarm.snoozeDuration.toLong())
        alarmRepository.updateSnooze(alarm.id, snoozeDateTime)
        // Reschedule Alarm
        scheduleAlarm(
            context,
            alarm.toAlarmExecutionData().copy(executionDateTime = snoozeDateTime)
        )
    }

    suspend fun disableOrRescheduleAlarm(context: Context, alarmRepository: AlarmRepository, alarm: Alarm) {
        // Dismiss/reschedule Alarm
        if (alarm.isRepeating()) {
            // Calculate the next time the repeating Alarm should execute
            val nextDateTime = AlarmUtil.nextRepeatingDateTime(
                alarm.dateTime,
                alarm.weeklyRepeater
            )
            // Dismiss Alarm and update with nextDateTime
            alarmRepository.dismissAndRescheduleRepeating(alarm.id, nextDateTime)
            // Reschedule Alarm with nextDateTime
            scheduleAlarm(
                context,
                alarm.toAlarmExecutionData().copy(executionDateTime = nextDateTime)
            )
        } else {
            // Dismiss non-repeating Alarm
            alarmRepository.dismissAlarm(alarm.id)
        }
    }

    /**
     * Cleans all dirty Alarms in the database, then reschedules any Alarms
     * that are still enabled in the database after cleaning.
     *
     * @param context Context to be used for scheduling Alarms
     * @param alarmRepository repository for getting all Alarms
     *
     * @see cleanAlarm
     */
    suspend fun cleanAndRescheduleAlarms(context: Context, alarmRepository: AlarmRepository) {
        // Clean Alarms
        alarmRepository.getAllAlarms().forEach { cleanAlarm(it, alarmRepository) }

        // Reschedule Alarms
        // Re-query the database to get up to date Alarm data after cleaning the Alarms
        rescheduleEligibleAlarms(context, alarmRepository.getAllAlarms())
    }

    suspend fun refreshAlarms(context: Context, alarmRepository: AlarmRepository) {
        // Get all enabled Alarms
        val enabledAlarms = alarmRepository.getAllEnabledAlarms()

        // Clean Alarms
        enabledAlarms.forEach { cleanAlarm(it, alarmRepository) }

        // Get all enabled Alarms post cleaning, as AlarmExecutionData
        val cleanAlarmExecutionData = alarmRepository.getAllEnabledAlarms().map { it.toAlarmExecutionData() }

        // Reschedule Alarms
        val alarmManager = getAlarmManager(context)
        cleanAlarmExecutionData.forEach { alarm ->
            // Create PendingIntent to execute Alarm
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                AlarmIntentBuilder.executeAlarmIntent(context, alarm),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule Alarm
            alarmManager.setAlarmClock(
                AlarmClockInfo(
                    alarm.executionDateTime.zonedEpochMillis(),
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                ),
                alarmPendingIntent
            )
        }
    }

    /*
     * Utility
     */

    /**
     * Cleans dirty Alarms as determined by Alarm.isDirty().
     * Alarms that are already clean are a no-op.
     *
     * This function only modifies Alarms in the database, and does not
     * do anything with scheduling or cancelling Alarms with AlarmManager.
     *
     * To clean an Alarm is to:
     * - Reset snooze
     * - If Alarm is repeating -> set to next repeating date/time
     * - If Alarm is not repeating -> disable Alarm
     *
     * @param alarm Alarm candidate for cleaning
     * @param alarmRepository repository in which cleaned Alarms are updated
     */
    private suspend fun cleanAlarm(alarm: Alarm, alarmRepository: AlarmRepository) {
        if (alarm.isDirty()) {
            // Clean Alarm
            val cleanAlarm = alarm.run {
                if (isRepeating()) {
                    copy(
                        dateTime = AlarmUtil.nextRepeatingDateTime(alarm.dateTime, alarm.weeklyRepeater),
                        snoozeDateTime = null
                    )
                } else {
                    copy(enabled = false, snoozeDateTime = null)
                }
            }

            // Update database
            alarmRepository.updateAlarm(cleanAlarm)
        }
    }

    /**
     * Reschedule Alarms if they meet the following criteria:
     * 1) Are enabled
     * 2) Are configured to execute in the future
     *
     * @param context Context to be used for scheduling Alarms
     * @param alarmList List of Alarms to potentially reschedule
     */
    private fun rescheduleEligibleAlarms(context: Context, alarmList: List<Alarm>) {
        val now = LocalDateTimeUtil.nowTruncated()
        alarmList
            .filter { alarm ->
                alarm.enabled &&
                        if (alarm.isSnoozed()) {
                            alarm.snoozeDateTime?.isAfter(now) == true
                        } else {
                            alarm.dateTime.isAfter(now)
                        }
            }
            .forEach { scheduleAlarm(context, it.toAlarmExecutionData()) }
    }

    private fun getAlarmManager(context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)
}

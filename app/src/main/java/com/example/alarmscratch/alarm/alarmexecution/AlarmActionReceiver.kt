package com.example.alarmscratch.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.util.AlarmUtil
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.alarmApplication
import com.example.alarmscratch.core.extension.doAsync
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import kotlinx.coroutines.Dispatchers

class AlarmActionReceiver : BroadcastReceiver() {

    companion object {
        // Actions
        const val ACTION_EXECUTE_ALARM = "action_execute_alarm"
        const val ACTION_SNOOZE_AND_RESCHEDULE_ALARM = "action_snooze_and_reschedule_alarm"
        const val ACTION_DISMISS_ALARM = "action_dismiss_alarm"

        // Extras
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_NAME = "extra_alarm_name"
        const val EXTRA_ALARM_EXECUTION_DATE_TIME = "extra_alarm_execution_date_time"
        const val EXTRA_REPEATING_DAYS = "extra_repeating_days"
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
        const val EXTRA_IS_VIBRATION_ENABLED = "extra_is_vibration_enabled"
        const val EXTRA_ALARM_SNOOZE_DURATION = "extra_alarm_snooze_duration"
        const val EXTRA_IS_24_HOUR = "extra_is_24_hour"

        // Other
        const val ALARM_NO_ID = -1
        const val ALARM_MISSING_REPEATING_DAYS = -1
        const val ALARM_NO_RINGTONE_URI = ""
        const val ALARM_NO_IS_VIBRATION_ENABLED = false
        const val ALARM_NO_IS_24_HOUR = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            when (intent.action) {
                ACTION_EXECUTE_ALARM ->
                    executeAlarm(context, intent)
                ACTION_SNOOZE_AND_RESCHEDULE_ALARM ->
                    snoozeAndRescheduleAlarm(context, intent)
                ACTION_DISMISS_ALARM ->
                    dismissAlarm(context, intent)
            }
        }
    }

    private fun executeAlarm(context: Context, intent: Intent) {
        WakeLockManager.acquireWakeLock(context)

        // Display Alarm Notification
        intent.extras?.let { extrasBundle ->
            val displayNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
                action = AlarmNotificationService.DISPLAY_ALARM_NOTIFICATION
                putExtras(extrasBundle)
            }
            context.applicationContext.startService(displayNotificationIntent)
        }
    }

    private fun snoozeAndRescheduleAlarm(context: Context, intent: Intent) {
        // Dismiss Alarm Notification
        dismissAlarmNotification(context)

        // Alarm data
        val id = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
        val snoozeDuration = intent.getIntExtra(EXTRA_ALARM_SNOOZE_DURATION, AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION)
        val snoozeDateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(snoozeDuration.toLong())
        val alarmExecutionData = AlarmExecutionData(
            id = id,
            name = intent.getStringExtra(EXTRA_ALARM_NAME) ?: context.getString(R.string.default_alarm_name),
            executionDateTime = snoozeDateTime,
            encodedRepeatingDays = intent.getIntExtra(EXTRA_REPEATING_DAYS, ALARM_MISSING_REPEATING_DAYS),
            ringtoneUri = intent.getStringExtra(EXTRA_RINGTONE_URI) ?: ALARM_NO_RINGTONE_URI,
            isVibrationEnabled = intent.getBooleanExtra(EXTRA_IS_VIBRATION_ENABLED, ALARM_NO_IS_VIBRATION_ENABLED),
            snoozeDuration = snoozeDuration
        )

        // Update Alarm Database and reschedule Alarm
        doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
            // Update Alarm
            val alarmRepo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
            alarmRepo.updateSnooze(id, snoozeDateTime)
            // Reschedule Alarm
            AlarmScheduler.scheduleAlarm(context.applicationContext, alarmExecutionData)
        }
    }

    private fun dismissAlarm(context: Context, intent: Intent) {
        // Dismiss Alarm Notification
        dismissAlarmNotification(context)

        // Alarm data
        val id = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)
        val encodedRepeatingDays = intent.getIntExtra(EXTRA_REPEATING_DAYS, ALARM_MISSING_REPEATING_DAYS)

        // Dismiss Alarm. Also reschedule if it's a repeating Alarm.
        doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
            val alarmRepo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
            if (id != ALARM_NO_ID && encodedRepeatingDays != ALARM_MISSING_REPEATING_DAYS) {
                if (encodedRepeatingDays > 0) {
                    // Must pull the original LocalDateTime from the database. This is because the one
                    // passed in the Intent just represents when the Alarm was supposed to execute,
                    // which may be a snoozed time that was modified from the original.
                    // Here we need the original, unmodified LocalDateTime for rescheduling.
                    val originalDateTime = alarmRepo.getAlarm(id).dateTime
                    alarmRepo.dismissAndRescheduleRepeating(
                        id,
                        AlarmUtil.nextRepeatingDateTime(originalDateTime, WeeklyRepeater(encodedRepeatingDays))
                    )
                } else {
                    alarmRepo.dismissAlarm(id)
                }
            } else if (id != ALARM_NO_ID) {
                // Unlikely edge case. Something went wrong involving the Intent.
                // The Alarm Notification will already be dismissed by this point,
                // so the only recovery option is to just dismiss the Alarm in the
                // database if possible.
                alarmRepo.dismissAlarm(id)
            }
        }
    }

    private fun dismissAlarmNotification(context: Context) {
        // Dismiss Alarm Notification
        val dismissNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
            action = AlarmNotificationService.DISMISS_ALARM_NOTIFICATION
        }
        context.applicationContext.startService(dismissNotificationIntent)
    }
}

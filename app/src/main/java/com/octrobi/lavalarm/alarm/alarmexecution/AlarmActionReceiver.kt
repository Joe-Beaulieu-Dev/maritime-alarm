package com.octrobi.lavalarm.alarm.alarmexecution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.alarm.ui.fullscreenalert.FullScreenAlarmButton
import com.octrobi.lavalarm.core.constant.actionPackageName
import com.octrobi.lavalarm.core.constant.extraPackageName
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.alarmApplication
import com.octrobi.lavalarm.core.extension.doAsync
import com.octrobi.lavalarm.core.extension.getSerializableExtraSafe
import com.octrobi.lavalarm.settings.data.repository.AlarmDefaultsRepository
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime

class AlarmActionReceiver : BroadcastReceiver() {

    companion object {
        // Actions
        const val ACTION_EXECUTE_ALARM = "${actionPackageName}EXECUTE_ALARM"
        const val ACTION_SNOOZE_AND_RESCHEDULE_ALARM = "${actionPackageName}SNOOZE_AND_RESCHEDULE_ALARM"
        const val ACTION_DISMISS_ALARM = "${actionPackageName}DISMISS_ALARM"

        // Extras
        const val EXTRA_ALARM_ID = "${extraPackageName}ALARM_ID"
        const val EXTRA_ALARM_NAME = "${extraPackageName}ALARM_NAME"
        const val EXTRA_ALARM_EXECUTION_DATE_TIME = "${extraPackageName}ALARM_EXECUTION_DATE_TIME"
        const val EXTRA_REPEATING_DAYS = "${extraPackageName}REPEATING_DAYS"
        const val EXTRA_RINGTONE_URI = "${extraPackageName}RINGTONE_URI"
        const val EXTRA_IS_VIBRATION_ENABLED = "${extraPackageName}IS_VIBRATION_ENABLED"
        const val EXTRA_ALARM_SNOOZE_DURATION = "${extraPackageName}ALARM_SNOOZE_DURATION"
        const val EXTRA_IS_24_HOUR = "${extraPackageName}IS_24_HOUR"
        const val EXTRA_ALARM_ACTION_ORIGIN = "${extraPackageName}ALARM_ACTION_ORIGIN"
        const val EXTRA_FULL_SCREEN_ALARM_BUTTON = "${extraPackageName}FULL_SCREEN_ALARM_BUTTON"

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
        val executionDateTime: LocalDateTime? = try {
            LocalDateTime.parse(intent.getStringExtra(EXTRA_ALARM_EXECUTION_DATE_TIME))
        } catch (e: Exception) {
            null
        }

        // Account for the edge case where the Device's date/time changes due to User intervention, time zone change, etc.
        // which results in the System time being in the future, beyond the time the Alarm is set to go off.
        // In this scenario, the AlarmManager will still execute the scheduled Alarm even though it's technically "missed".
        // You can create a BroadcastReceiver to listen for these date/time changes and attempt to cancel the Alarm
        // if needed, but you would be racing the AlarmManager because it will not know that you have this BroadcastReceiver
        // set up to do this. This will lead to very inconsistent behavior where the Alarm may or may not get cancelled
        // in time before the AlarmManager auto-executes it. Therefore, we check here instead to determine whether the
        // Alarm should go off. That said, we still have a TimeChangeReceiver that performs cleanup of Alarms in the database
        // after a date/time change when needed (see TimeChangeReceiver).
        if (executionDateTime?.isBefore(LocalDateTimeUtil.nowTruncated()) == false) {
            WakeLockManager.acquireWakeLock(context)

            // Display Alarm Notification
            intent.extras?.let { extrasBundle ->
                val displayNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
                    action = AlarmNotificationService.ACTION_DISPLAY_ALARM_NOTIFICATION
                    putExtras(extrasBundle)
                }
                context.applicationContext.startService(displayNotificationIntent)
            }
        }
    }

    private fun snoozeAndRescheduleAlarm(context: Context, intent: Intent) {
        // TODO: Come up with a better default than just AlarmActionOrigin.NOTIFICATION
        // Dismiss Alarm Notification
        // Grab snooze duration up here for PostAlarmConfirmationScreen downstream
        val alarmActionOrigin = intent.getSerializableExtraSafe(
            EXTRA_ALARM_ACTION_ORIGIN,
            AlarmActionOrigin::class.java
        ) ?: AlarmActionOrigin.NOTIFICATION
        val snoozeDuration = intent.getIntExtra(EXTRA_ALARM_SNOOZE_DURATION, AlarmDefaultsRepository.DEFAULT_SNOOZE_DURATION)
        dismissAlarmNotification(context, alarmActionOrigin, FullScreenAlarmButton.SNOOZE, snoozeDuration)

        // Alarm data
        val id = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)

        // Update Alarm Database and reschedule Alarm
        doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
            // Update Alarm
            val alarmRepo = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
            val alarm = alarmRepo.getAlarm(id)
            AlarmScheduler.snoozeAndRescheduleAlarm(context.applicationContext, alarmRepo, alarm)
        }
    }

    private fun dismissAlarm(context: Context, intent: Intent) {
        // TODO: Come up with a better default than just AlarmActionOrigin.NOTIFICATION
        // Dismiss Alarm Notification
        val alarmActionOrigin = intent.getSerializableExtraSafe(
            EXTRA_ALARM_ACTION_ORIGIN,
            AlarmActionOrigin::class.java
        ) ?: AlarmActionOrigin.NOTIFICATION
        dismissAlarmNotification(context, alarmActionOrigin, FullScreenAlarmButton.DISMISS)

        // Alarm data
        val id = intent.getIntExtra(EXTRA_ALARM_ID, ALARM_NO_ID)

        // Dismiss Alarm. Also reschedule if it's a repeating Alarm.
        doAsync(context.alarmApplication.applicationScope, Dispatchers.IO) {
            if (id != ALARM_NO_ID) {
                // Must pull the original LocalDateTime from the database. This is because the one
                // passed in the Intent just represents when the Alarm was supposed to execute,
                // which may be a snoozed time that was modified from the original.
                // Here we need the original, unmodified LocalDateTime for rescheduling.
                val alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDao())
                val alarm = alarmRepository.getAlarm(id)
                AlarmScheduler.disableOrRescheduleAlarm(context.applicationContext, alarmRepository, alarm)
            }
        }
    }

    private fun dismissAlarmNotification(
        context: Context,
        alarmActionOrigin: AlarmActionOrigin,
        fullScreenAlarmButton: FullScreenAlarmButton,
        snoozeDuration: Int? = null
    ) {
        // Dismiss Alarm Notification
        val dismissNotificationIntent = Intent(context.applicationContext, AlarmNotificationService::class.java).apply {
            // Action
            action = AlarmNotificationService.ACTION_DISMISS_ALARM_NOTIFICATION
            // Extras
            putExtra(EXTRA_ALARM_ACTION_ORIGIN, alarmActionOrigin)
            putExtra(EXTRA_FULL_SCREEN_ALARM_BUTTON, fullScreenAlarmButton)
            putExtra(EXTRA_ALARM_SNOOZE_DURATION, snoozeDuration)
        }
        context.applicationContext.startService(dismissNotificationIntent)
    }
}

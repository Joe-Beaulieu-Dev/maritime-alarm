package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.core.extension.to12HourNotificationDateTimeString
import com.example.alarmscratch.core.extension.to24HourNotificationDateTimeString
import com.example.alarmscratch.settings.data.model.TimeDisplay
import java.time.LocalDateTime

object AlarmNotification {

    const val CHANNEL_ID_ALARM_NOTIFICATION = "channel_id_alarm_notification"

    fun fullScreenNotification(
        context: Context,
        alarmId: Int,
        alarmName: String,
        alarmExecutionDateTime: String,
        snoozeDuration: Int,
        timeDisplay: TimeDisplay
    ): Notification {
        // This shows up in the Status Bar Notification, but not in the full screen alert
        val notificationDateTime = try {
            when (timeDisplay) {
                TimeDisplay.TwelveHour ->
                    LocalDateTime.parse(alarmExecutionDateTime).to12HourNotificationDateTimeString(context)
                TimeDisplay.TwentyFourHour ->
                    LocalDateTime.parse(alarmExecutionDateTime).to24HourNotificationDateTimeString()
            }
        } catch (e: Exception) {
            context.getString(R.string.default_alarm_time)
        }

        return Notification.Builder(context, CHANNEL_ID_ALARM_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_alarm_24dp)
            .setContentTitle(alarmName)
            .setContentText(notificationDateTime)
            .setCategory(Notification.CATEGORY_ALARM)
            .setActions(
                getSnoozeAlarmAction(context, alarmId, snoozeDuration),
                getDismissAlarmAction(context, alarmId)
            )
            .setDeleteIntent(getDismissAlarmPendingIntent(context, alarmId))
            .setFullScreenIntent(
                getAlertPendingIntent(context, alarmId, alarmName, alarmExecutionDateTime, timeDisplay),
                true
            )
            .build()
    }

    private fun getSnoozeAlarmAction(context: Context, alarmId: Int, snoozeDuration: Int): Notification.Action {
        val snoozeAlarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_SNOOZE_AND_RESCHEDULE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, snoozeDuration)
        }

        val snoozeAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            snoozeAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_snooze_24dp),
            context.getString(R.string.snooze_alarm),
            snoozeAlarmPendingIntent
        ).build()
    }

    private fun getDismissAlarmAction(context: Context, alarmId: Int): Notification.Action =
        Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_dismiss_24dp),
            context.getString(R.string.dismiss_alarm),
            getDismissAlarmPendingIntent(context, alarmId)
        ).build()

    private fun getDismissAlarmPendingIntent(context: Context, alarmId: Int): PendingIntent {
        val clearNotificationIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
        }

        return PendingIntent.getBroadcast(
            context,
            alarmId,
            clearNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getAlertPendingIntent(
        context: Context,
        alarmId: Int,
        alarmName: String,
        alarmDateTime: String,
        timeDisplay: TimeDisplay
    ): PendingIntent {
        val is24Hour = when (timeDisplay) {
            TimeDisplay.TwelveHour ->
                false
            TimeDisplay.TwentyFourHour ->
                true
        }

        val fullScreenAlertIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmName)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmDateTime)
            putExtra(AlarmActionReceiver.EXTRA_IS_24_HOUR, is24Hour)
            // Flags
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        }

        return PendingIntent.getActivity(
            context,
            alarmId,
            fullScreenAlertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

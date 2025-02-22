package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmIntentBuilder
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.core.extension.to12HourNotificationDateTimeString
import com.example.alarmscratch.core.extension.to24HourNotificationDateTimeString
import com.example.alarmscratch.settings.data.model.TimeDisplay

object AlarmNotification {

    const val CHANNEL_ID_ALARM_NOTIFICATION = "channel_id_alarm_notification"

    fun fullScreenNotification(
        context: Context,
        alarmExecutionData: AlarmExecutionData,
        timeDisplay: TimeDisplay
    ): Notification {
        // This shows up in the Status Bar Notification, but not in the full screen alert
        val notificationDateTime = when (timeDisplay) {
            TimeDisplay.TwelveHour ->
                alarmExecutionData.executionDateTime.to12HourNotificationDateTimeString(context)
            TimeDisplay.TwentyFourHour ->
                alarmExecutionData.executionDateTime.to24HourNotificationDateTimeString()
        }

        return Notification.Builder(context, CHANNEL_ID_ALARM_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_alarm_24dp)
            .setContentTitle(alarmExecutionData.name)
            .setContentText(notificationDateTime)
            .setCategory(Notification.CATEGORY_ALARM)
            .setActions(
                getSnoozeAlarmAction(context, alarmExecutionData),
                getDismissAlarmAction(context, alarmExecutionData)
            )
            .setDeleteIntent(getDismissAlarmPendingIntent(context, alarmExecutionData))
            .setFullScreenIntent(getFullScreenAlertPendingIntent(context, alarmExecutionData, timeDisplay), true)
            .build()
    }

    private fun getSnoozeAlarmAction(context: Context, alarmExecutionData: AlarmExecutionData): Notification.Action {
        val snoozeAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmExecutionData.id,
            AlarmIntentBuilder.snoozeAlarmFromNotification(context, alarmExecutionData),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_snooze_24dp),
            context.getString(R.string.snooze_alarm),
            snoozeAlarmPendingIntent
        ).build()
    }

    private fun getDismissAlarmAction(context: Context, alarmExecutionData: AlarmExecutionData): Notification.Action =
        Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_dismiss_24dp),
            context.getString(R.string.dismiss_alarm),
            getDismissAlarmPendingIntent(context, alarmExecutionData)
        ).build()

    private fun getDismissAlarmPendingIntent(context: Context, alarmExecutionData: AlarmExecutionData): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            alarmExecutionData.id,
            AlarmIntentBuilder.dismissAlarmFromNotification(context, alarmExecutionData),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun getFullScreenAlertPendingIntent(
        context: Context,
        alarmExecutionData: AlarmExecutionData,
        timeDisplay: TimeDisplay
    ): PendingIntent {
        val name = alarmExecutionData.name.ifBlank { context.getString(R.string.default_alarm_name) }
        val is24Hour = when (timeDisplay) {
            TimeDisplay.TwelveHour ->
                false
            TimeDisplay.TwentyFourHour ->
                true
        }

        val fullScreenAlertIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmExecutionData.id)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, name)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_EXECUTION_DATE_TIME, alarmExecutionData.executionDateTime.toString())
            putExtra(AlarmActionReceiver.EXTRA_REPEATING_DAYS, alarmExecutionData.encodedRepeatingDays)
            putExtra(AlarmActionReceiver.EXTRA_RINGTONE_URI, alarmExecutionData.ringtoneUri)
            putExtra(AlarmActionReceiver.EXTRA_IS_VIBRATION_ENABLED, alarmExecutionData.isVibrationEnabled)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, alarmExecutionData.snoozeDuration)
            putExtra(AlarmActionReceiver.EXTRA_IS_24_HOUR, is24Hour)
            // Flags
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        }

        return PendingIntent.getActivity(
            context,
            alarmExecutionData.id,
            fullScreenAlertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

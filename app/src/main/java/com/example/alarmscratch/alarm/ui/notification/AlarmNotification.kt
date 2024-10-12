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
        alarmDateTime: String,
        timeDisplay: TimeDisplay
    ): Notification {
        // This shows up in the Status Bar Notification, but not in the full screen alert
        val notificationDateTimeString = try {
            when (timeDisplay) {
                TimeDisplay.TwelveHour ->
                    LocalDateTime.parse(alarmDateTime).to12HourNotificationDateTimeString(context)
                TimeDisplay.TwentyFourHour ->
                    LocalDateTime.parse(alarmDateTime).to24HourNotificationDateTimeString()
            }
        } catch (e: Exception) {
            context.getString(R.string.default_alarm_time)
        }

        return Notification.Builder(context, CHANNEL_ID_ALARM_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_alarm_24dp)
            .setContentTitle(alarmName)
            .setContentText(notificationDateTimeString)
            .setCategory(Notification.CATEGORY_ALARM)
            .addAction(getDismissAlarmAction(context, alarmId))
            .setDeleteIntent(getClearNotificationPendingIntent(context, alarmId))
            .setFullScreenIntent(getAlertPendingIntent(context, alarmId, alarmName, alarmDateTime), true)
            .build()
    }

    private fun getDismissAlarmAction(context: Context, alarmId: Int): Notification.Action {
        val dismissAlarmIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
        }

        val dismissAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            dismissAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context, R.drawable.ic_alarm_dismiss_24dp),
            context.getString(R.string.dismiss_alarm),
            dismissAlarmPendingIntent
        ).build()
    }

    private fun getClearNotificationPendingIntent(context: Context, alarmId: Int): PendingIntent {
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

    private fun getAlertPendingIntent(context: Context, alarmId: Int, alarmName: String, alarmDateTime: String): PendingIntent {
        val fullScreenAlertIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_NAME, alarmName)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_DATE_TIME, alarmDateTime)
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

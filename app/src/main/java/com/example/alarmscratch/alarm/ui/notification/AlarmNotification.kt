package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity
import com.example.alarmscratch.core.extension.toNotificationDateTimeString
import java.time.LocalDateTime

object AlarmNotification {

    const val CHANNEL_ID_ALARM_NOTIFICATION = "channel_id_alarm_notification"

    fun fullScreenNotification(context: Context, alarmId: Int, alarmName: String, alarmDateTime: String): Notification {
        // This shows up in the Status Bar Notification, but not in the full screen alert
        val notificationDateTimeString = try {
            LocalDateTime.parse(alarmDateTime).toNotificationDateTimeString(context)
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

    // Since API level 34, developers can no longer make Notifications non-dismissible unless
    // the Notification meets certain exception criteria. This app's Alarm Notifications do not meet
    // the exception criteria, and therefore setDeleteIntent() must be called on the Notification Builder
    // to handle cases where the User swipes the Notification away.
    //
    // See Google's documentation for details:
    // https://developer.android.com/about/versions/14/behavior-changes-all#non-dismissable-notifications
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

package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmActivity

class AlarmNotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val ALARM_NOTIFICATION_CHANNEL_ID = "alarm_notification_channel"
    }

    // TODO: Check permission
    fun showNotification(alarmName: String, alarmTime: String) {
        val notification = Notification.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alarmName)
            .setContentText(alarmTime)
            .build()

        // TODO: Modify ID
        notificationManager.notify(1, notification)
    }

    // TODO: Check permission
    fun showFullScreenNotification(alarmName: String, alarmTime: String) {
        val intent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_NAME, alarmName)
            putExtra(AlarmReceiver.EXTRA_ALARM_TIME, alarmTime)
        }

        // TODO: Modify request code
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alarmName)
            .setContentText(alarmTime)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        // TODO: Modify ID
        notificationManager.notify(2, notification)
    }
}

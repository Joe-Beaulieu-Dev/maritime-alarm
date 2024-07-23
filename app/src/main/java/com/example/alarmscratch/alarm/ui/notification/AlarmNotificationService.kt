package com.example.alarmscratch.alarm.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.R

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
}
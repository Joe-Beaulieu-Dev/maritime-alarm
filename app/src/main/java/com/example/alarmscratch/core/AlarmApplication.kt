package com.example.alarmscratch.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotificationService

class AlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // TODO: Come up with real strings for this
        val channel = NotificationChannel(
            AlarmNotificationService.CHANNEL_ID_ALARM_NOTIFICATION,
            getString(R.string.permission_channel_alarm_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = getString(R.string.permission_channel_alarm_desc)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

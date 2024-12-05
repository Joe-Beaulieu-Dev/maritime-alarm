package com.example.alarmscratch.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AlarmApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // TODO: Come up with real strings for this
        val channel = NotificationChannel(
            AlarmNotification.CHANNEL_ID_ALARM_NOTIFICATION,
            getString(R.string.permission_channel_alarm_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = getString(R.string.permission_channel_alarm_desc)
        // Silence channel so we can put in User selected sounds
        channel.setSound(null, null)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

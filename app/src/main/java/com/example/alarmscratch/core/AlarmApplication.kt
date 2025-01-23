package com.example.alarmscratch.core

import android.app.Application
import com.example.alarmscratch.core.ui.notificationcheck.AppNotificationChannel
import com.example.alarmscratch.core.util.NotificationChannelUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AlarmApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        // Create Alarm NotificationChannel
        NotificationChannelUtil.createNotificationChannel(this, AppNotificationChannel.Alarm)
    }
}

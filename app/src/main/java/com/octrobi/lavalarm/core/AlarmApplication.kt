package com.octrobi.lavalarm.core

import android.app.Application
import com.octrobi.lavalarm.core.ui.notificationcheck.AppNotificationChannel
import com.octrobi.lavalarm.core.util.NotificationChannelUtil
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

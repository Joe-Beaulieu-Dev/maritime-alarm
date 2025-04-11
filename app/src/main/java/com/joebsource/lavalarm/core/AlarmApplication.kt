package com.joebsource.lavalarm.core

import android.app.Application
import com.joebsource.lavalarm.core.ui.notificationcheck.AppNotificationChannel
import com.joebsource.lavalarm.core.util.NotificationChannelUtil
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

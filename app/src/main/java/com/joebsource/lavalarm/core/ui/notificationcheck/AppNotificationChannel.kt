package com.joebsource.lavalarm.core.ui.notificationcheck

import android.app.NotificationManager
import androidx.annotation.StringRes
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.ui.notification.AlarmNotification

sealed class AppNotificationChannel(
    val id: String,
    @StringRes val name: Int,
    val importance: Int,
    @StringRes val description: Int,
    val soundAttributes: NotificationChannelSoundAttributes?
) {
    data object Alarm : AppNotificationChannel(
        id = AlarmNotification.ALARM_NOTIFICATION_CHANNEL_ID,
        name = R.string.permission_channel_alarm_name,
        importance = NotificationManager.IMPORTANCE_HIGH,
        description = R.string.permission_channel_alarm_desc,
        soundAttributes = NotificationChannelSoundAttributes()
    )
}

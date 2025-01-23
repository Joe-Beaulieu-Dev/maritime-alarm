package com.example.alarmscratch.core.ui.notificationcheck

import android.app.NotificationManager
import androidx.annotation.StringRes
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.ui.notification.AlarmNotification

sealed class AppNotificationChannel(
    val id: String,
    @StringRes val name: Int,
    val importance: Int,
    @StringRes val description: Int,
    val soundAttributes: NotificationChannelSoundAttributes?
) {
    // TODO: Revisit these strings
    data object Alarm : AppNotificationChannel(
        id = AlarmNotification.CHANNEL_ID_ALARM_NOTIFICATION,
        name = R.string.permission_channel_alarm_name,
        importance = NotificationManager.IMPORTANCE_HIGH,
        description = R.string.permission_channel_alarm_desc,
        soundAttributes = NotificationChannelSoundAttributes()
    )
}

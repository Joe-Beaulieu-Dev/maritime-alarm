package com.example.alarmscratch.core.ui.notificationcheck

import androidx.annotation.StringRes
import com.example.alarmscratch.R

sealed class NotificationPermission(
    val appNotificationChannel: AppNotificationChannel,
    @StringRes val notificationDisabledBodyText: Int
) {
    data object Alarm : NotificationPermission(
        appNotificationChannel = AppNotificationChannel.Alarm,
        notificationDisabledBodyText = R.string.notification_missing_alarm_system_settings
    )
}

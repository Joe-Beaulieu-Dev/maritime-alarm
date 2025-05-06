package com.octrobi.lavalarm.core.ui.notificationcheck

import androidx.annotation.StringRes
import com.octrobi.lavalarm.R

sealed class NotificationPermission(
    val appNotificationChannel: AppNotificationChannel,
    @StringRes val notificationDisabledBodyText: Int
) {
    data object Alarm : NotificationPermission(
        appNotificationChannel = AppNotificationChannel.Alarm,
        notificationDisabledBodyText = R.string.notification_missing_alarm_system_settings
    )
}

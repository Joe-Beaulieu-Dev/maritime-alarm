package com.octrobi.lavalarm.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.octrobi.lavalarm.core.ui.notificationcheck.AppNotificationChannel

object NotificationChannelUtil {

    /*
     * Create
     */

    fun createNotificationChannel(context: Context, appNotificationChannel: AppNotificationChannel) {
        // Configure channel
        val channel = NotificationChannel(
            appNotificationChannel.id,
            context.getString(appNotificationChannel.name),
            appNotificationChannel.importance
        )
        channel.description = context.getString(appNotificationChannel.description)
        appNotificationChannel.soundAttributes?.let {
            channel.setSound(it.sound, it.audioAttributes)
        }

        // Create channel
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    /*
     * Query
     */

    fun isNotificationChannelEnabled(
        context: Context,
        appNotificationChannel: AppNotificationChannel
    ): Boolean {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = notificationManager.getNotificationChannel(appNotificationChannel.id)

        // It can be the case that Notifications in general are disabled, while the specific
        // NotificationChannel is enabled. This will lead to an effective false positive, since
        // in this scenario the User will not be able to actually receive a Notification.
        // Therefore, both must be checked.
        return notificationManager.areNotificationsEnabled() &&
                channel.importance != NotificationManager.IMPORTANCE_NONE
    }
}

package com.example.alarmscratch.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.core.ui.notificationcheck.AppNotificationChannel

object NotificationChannelUtil {

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
}

package com.example.alarmscratch.testutil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.alarmscratch.core.ui.notificationcheck.AppNotificationChannel

object NotificationChannelUtil {

    fun disableNotificationChannel(context: Context, appNotificationChannel: AppNotificationChannel) {
        // Keep original channel configuration, except decrease importance level to IMPORTANCE_NONE.
        // NotificationChannels with IMPORTANCE_NONE are considered disabled.
        val channel = NotificationChannel(
            appNotificationChannel.id,
            context.getString(appNotificationChannel.name),
            NotificationManager.IMPORTANCE_NONE
        )
        channel.description = context.getString(appNotificationChannel.description)
        appNotificationChannel.soundAttributes?.let {
            channel.setSound(it.sound, it.audioAttributes)
        }

        // Update channel
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

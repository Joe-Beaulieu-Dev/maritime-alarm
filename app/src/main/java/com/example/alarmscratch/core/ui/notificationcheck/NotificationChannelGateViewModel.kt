package com.example.alarmscratch.core.ui.notificationcheck

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class NotificationChannelGateViewModel : ViewModel() {

    // Notification
    val disabledChannelList = mutableStateListOf<AppNotificationChannel>()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                NotificationChannelGateViewModel() as T
        }
    }

    /*
     * Check
     */

    fun checkNotificationChannelStatus(context: Context, appNotificationChannel: AppNotificationChannel) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = notificationManager.getNotificationChannel(appNotificationChannel.id)
        val isChannelEnabled =
            notificationManager.areNotificationsEnabled() && channel.importance != NotificationManager.IMPORTANCE_NONE

        if (!isChannelEnabled) {
            disabledChannelList.add(appNotificationChannel)
        } else if (disabledChannelList.isNotEmpty()) {
            // List can contain duplicates, remove all instances
            disabledChannelList.removeAll { it == appNotificationChannel }
        }
    }

    /*
     * Navigation
     */

    fun openNotificationSettings(context: Context) {
        val notificationSettingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }

        (context as? Activity)?.startActivity(notificationSettingsIntent)
    }
}

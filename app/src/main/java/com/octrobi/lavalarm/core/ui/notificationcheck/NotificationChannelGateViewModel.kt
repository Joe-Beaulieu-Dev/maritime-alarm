package com.octrobi.lavalarm.core.ui.notificationcheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.octrobi.lavalarm.core.util.NotificationChannelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationChannelGateViewModel @Inject constructor() : ViewModel() {

    // Notification
    val disabledChannelList = mutableStateListOf<AppNotificationChannel>()

    /*
     * Check
     */

    fun checkNotificationChannelStatus(context: Context, appNotificationChannel: AppNotificationChannel) {
        val isChannelEnabled = NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel)

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

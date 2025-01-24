package com.example.alarmscratch.core.ui.notificationcheck

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimpleNotificationGateViewModel : ViewModel() {

    // Notification Channel
    private val _isNotificationChannelEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNotificationChannelEnabled: StateFlow<Boolean> = _isNotificationChannelEnabled.asStateFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                SimpleNotificationGateViewModel() as T
        }
    }

    /*
     * Check
     */

    fun checkNotificationChannelStatus(context: Context, appNotificationChannel: AppNotificationChannel) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = notificationManager.getNotificationChannel(appNotificationChannel.id)

        _isNotificationChannelEnabled.value =
            notificationManager.areNotificationsEnabled() && channel.importance != NotificationManager.IMPORTANCE_NONE
    }
}

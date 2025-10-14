package com.octrobi.lavalarm.core.ui.notificationcheck

import android.content.Context
import androidx.lifecycle.ViewModel
import com.octrobi.lavalarm.core.util.NotificationChannelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SimpleNotificationGateViewModel @Inject constructor() : ViewModel() {

    // Notification Channel
    private val _isNotificationChannelEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNotificationChannelEnabled: StateFlow<Boolean> = _isNotificationChannelEnabled.asStateFlow()

    /*
     * Check
     */

    fun checkNotificationChannelStatus(context: Context, appNotificationChannel: AppNotificationChannel) {
        _isNotificationChannelEnabled.value = NotificationChannelUtil.isNotificationChannelEnabled(context, appNotificationChannel)
    }
}

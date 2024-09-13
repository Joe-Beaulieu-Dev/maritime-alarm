package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmNotificationActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver

class FullScreenAlarmViewModel : ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return FullScreenAlarmViewModel() as T
            }
        }
    }

    fun dismissAlarm(context: Context, alarmId: Int) {
        val dismissAlarmIntent = Intent(context.applicationContext, AlarmNotificationActionReceiver::class.java).apply {
            action = AlarmNotificationActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        context.applicationContext.sendBroadcast(dismissAlarmIntent)
    }
}

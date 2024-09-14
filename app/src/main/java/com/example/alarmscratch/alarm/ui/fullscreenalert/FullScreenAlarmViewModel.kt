package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmscratch.alarm.alarmexecution.AlarmNotificationActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmReceiver
import java.time.LocalDateTime

class FullScreenAlarmViewModel(
    val alarmId: Int,
    val alarmName: String,
    val alarmDateTime: LocalDateTime?
) : ViewModel() {

    companion object {

        fun provideFactory(
            alarmId: Int,
            alarmName: String,
            alarmDateTime: LocalDateTime?
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FullScreenAlarmViewModel(alarmId, alarmName, alarmDateTime) as T
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

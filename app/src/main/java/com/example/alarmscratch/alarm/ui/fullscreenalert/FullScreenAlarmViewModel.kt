package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import java.time.LocalDateTime

class FullScreenAlarmViewModel(
    private val alarmId: Int,
    val alarmName: String,
    val alarmDateTime: LocalDateTime?,
    val is24Hour: Boolean
) : ViewModel() {

    companion object {

        fun provideFactory(
            alarmId: Int,
            alarmName: String,
            alarmDateTime: LocalDateTime?,
            is24Hour: Boolean
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FullScreenAlarmViewModel(alarmId, alarmName, alarmDateTime, is24Hour) as T
            }
    }

    fun dismissAlarm(context: Context) {
        val dismissAlarmIntent = Intent(context.applicationContext, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
        }
        context.applicationContext.sendBroadcast(dismissAlarmIntent)
    }
}

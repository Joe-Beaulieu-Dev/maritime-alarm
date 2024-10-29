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
    private val snoozeDuration: Int,
    val is24Hour: Boolean
) : ViewModel() {

    companion object {

        fun provideFactory(
            alarmId: Int,
            alarmName: String,
            alarmDateTime: LocalDateTime?,
            snoozeDuration: Int,
            is24Hour: Boolean
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FullScreenAlarmViewModel(alarmId, alarmName, alarmDateTime, snoozeDuration, is24Hour) as T
            }
    }

    fun snoozeAlarm(context: Context) {
        val snoozeAlarmIntent = Intent(context.applicationContext, AlarmActionReceiver::class.java).apply {
            // Action
            action = AlarmActionReceiver.ACTION_SNOOZE_AND_RESCHEDULE_ALARM
            // Extras
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmActionReceiver.EXTRA_ALARM_SNOOZE_DURATION, snoozeDuration)
        }
        context.applicationContext.sendBroadcast(snoozeAlarmIntent)
    }

    fun dismissAlarm(context: Context) {
        val dismissAlarmIntent = Intent(context.applicationContext, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmActionReceiver.EXTRA_ALARM_ID, alarmId)
        }
        context.applicationContext.sendBroadcast(dismissAlarmIntent)
    }
}

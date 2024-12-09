package com.example.alarmscratch.alarm.ui.fullscreenalert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmscratch.alarm.alarmexecution.AlarmIntentBuilder
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData

class FullScreenAlarmViewModel(
    val alarmExecutionData: AlarmExecutionData,
    val is24Hour: Boolean
) : ViewModel() {

    companion object {

        fun provideFactory(
            alarmExecutionData: AlarmExecutionData,
            is24Hour: Boolean
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FullScreenAlarmViewModel(alarmExecutionData, is24Hour) as T
            }
    }

    fun snoozeAlarm(context: Context) {
        context.sendBroadcast(
            AlarmIntentBuilder.snoozeAlarmIntent(context.applicationContext, alarmExecutionData)
        )
    }

    fun dismissAlarm(context: Context) {
        context.sendBroadcast(
            AlarmIntentBuilder.dismissAlarmIntent(context.applicationContext, alarmExecutionData)
        )
    }
}

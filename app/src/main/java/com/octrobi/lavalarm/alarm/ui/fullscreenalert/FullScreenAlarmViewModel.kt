package com.octrobi.lavalarm.alarm.ui.fullscreenalert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmIntentBuilder
import com.octrobi.lavalarm.alarm.data.model.AlarmExecutionData

class FullScreenAlarmViewModel(
    val alarmExecutionData: AlarmExecutionData,
    val is24Hour: Boolean
) : ViewModel() {

    companion object {

        fun provideFactory(
            alarmExecutionData: AlarmExecutionData,
            is24Hour: Boolean
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FullScreenAlarmViewModel(alarmExecutionData, is24Hour)
                }
            }
    }

    fun snoozeAlarm(context: Context) {
        context.sendBroadcast(
            AlarmIntentBuilder.snoozeAlarmFromFullScreen(context.applicationContext, alarmExecutionData)
        )
    }

    fun dismissAlarm(context: Context) {
        context.sendBroadcast(
            AlarmIntentBuilder.dismissAlarmFromFullScreen(context.applicationContext, alarmExecutionData)
        )
    }
}

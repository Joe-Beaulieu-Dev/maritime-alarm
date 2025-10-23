package com.octrobi.lavalarm.alarm.ui.fullscreenalert

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmIntentBuilder
import com.octrobi.lavalarm.core.navigation.AlarmExecutionDataNavType
import com.octrobi.lavalarm.core.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenAlarmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Alarm and Display data
    private val navigationRoute = savedStateHandle.toRoute<Destination.FullScreenAlarmScreen>(
        typeMap = AlarmExecutionDataNavType.typeMap
    )
    val alarmExecutionData = navigationRoute.alarmExecutionData
    val is24Hour = navigationRoute.is24Hour

    /*
     * Action
     */

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

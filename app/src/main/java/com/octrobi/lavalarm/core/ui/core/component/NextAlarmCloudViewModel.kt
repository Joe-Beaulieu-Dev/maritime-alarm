package com.octrobi.lavalarm.core.ui.core.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.alarm.data.repository.AlarmState
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.isSnoozed
import com.octrobi.lavalarm.core.extension.toCountdownString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NextAlarmCloudViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    alarmRepository: AlarmRepository
) : ViewModel() {

    // Time Change BroadcastReceiver
    val timeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                when (intent.action) {
                    Intent.ACTION_TIME_TICK,
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_DATE_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED ->
                        refreshAlarmCountdownState()
                }
            }
        }
    }

    // State
    private val nextAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
    private val _alarmCountdownState: MutableStateFlow<AlarmCountdownState> = MutableStateFlow(AlarmCountdownState.Loading)
    val alarmCountdownState: StateFlow<AlarmCountdownState> = _alarmCountdownState.asStateFlow()

    init {
        viewModelScope.launch {
            alarmRepository.getAllAlarmsFlow()
                .map { alarmList ->
                    val nextAlarm = getNextAlarm(alarmList)
                    if (nextAlarm != null) {
                        AlarmState.Success(nextAlarm)
                    } else {
                        AlarmState.Error(Throwable())
                    }
                }
                .catch { throwable -> emit(AlarmState.Error(throwable)) }
                .collect { alarmState ->
                    // Get AlarmState for next Alarm
                    nextAlarm.value = alarmState

                    // Refresh Alarm Countdown
                    refreshAlarmCountdownState()
                }
        }
    }

    private fun getNextAlarm(alarmList: List<Alarm>): Alarm? =
        alarmList
            .filter { alarm ->
                alarm.enabled &&
                        if (alarm.isSnoozed()) {
                            // TODO: Think of a better default behavior for this
                            alarm.snoozeDateTime?.isAfter(LocalDateTimeUtil.nowTruncated()) ?: false
                        } else {
                            alarm.dateTime.isAfter(LocalDateTimeUtil.nowTruncated())
                        }
            }
            .minByOrNull { alarm -> alarm.snoozeDateTime ?: alarm.dateTime }

    private fun refreshAlarmCountdownState() {
        _alarmCountdownState.value = AlarmCountdownState.Success(
            icon = getIcon(nextAlarm.value),
            countdownText = getCountdownText(nextAlarm.value)
        )
    }

    private fun getCountdownText(alarmState: AlarmState): String =
        if (alarmState is AlarmState.Success) {
            alarmState.alarm.toCountdownString(applicationContext)
        } else {
            applicationContext.getString(R.string.no_active_alarms)
        }

    private fun getIcon(alarmState: AlarmState): ImageVector =
        if (alarmState is AlarmState.Success) {
            if (alarmState.alarm.isSnoozed()) {
                Icons.Default.Snooze
            } else {
                Icons.Default.Alarm
            }
        } else {
            Icons.Default.AlarmOff
        }
}

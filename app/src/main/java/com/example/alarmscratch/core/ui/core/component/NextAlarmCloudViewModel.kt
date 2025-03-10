package com.example.alarmscratch.core.ui.core.component

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isSnoozed
import com.example.alarmscratch.core.extension.toCountdownString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NextAlarmCloudViewModel(
    private val application: Application,
    private val alarmRepository: AlarmRepository
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

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                NextAlarmCloudViewModel(
                    application = application,
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                )
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
            alarmState.alarm.toCountdownString(application)
        } else {
            application.getString(R.string.no_active_alarms)
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

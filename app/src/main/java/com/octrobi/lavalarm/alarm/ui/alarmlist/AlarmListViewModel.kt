package com.octrobi.lavalarm.alarm.ui.alarmlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.octrobi.lavalarm.alarm.alarmexecution.AlarmScheduler
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.repository.AlarmListState
import com.octrobi.lavalarm.alarm.data.repository.AlarmRepository
import com.octrobi.lavalarm.core.extension.toAlarmExecutionData
import com.octrobi.lavalarm.core.extension.toScheduleString
import com.octrobi.lavalarm.core.extension.withFuturizedDateTime
import com.octrobi.lavalarm.core.ui.snackbar.SnackbarEvent
import com.octrobi.lavalarm.core.ui.snackbar.global.GlobalSnackbarController
import com.octrobi.lavalarm.settings.data.model.GeneralSettings
import com.octrobi.lavalarm.settings.data.repository.GeneralSettingsRepository
import com.octrobi.lavalarm.settings.data.repository.GeneralSettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    generalSettingsRepository: GeneralSettingsRepository
) : ViewModel() {

    // Alarm List
    val alarmList: StateFlow<AlarmListState> =
        alarmRepository.getAllAlarmsFlow()
            .map<List<Alarm>, AlarmListState> { alarmList -> AlarmListState.Success(alarmList) }
            .catch { throwable -> emit(AlarmListState.Error(throwable)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                AlarmListState.Loading
            )

    // Settings
    val generalSettings: StateFlow<GeneralSettingsState> =
        generalSettingsRepository.generalSettingsFlow
            .map<GeneralSettings, GeneralSettingsState> { generalSettings -> GeneralSettingsState.Success(generalSettings) }
            .catch { throwable -> emit(GeneralSettingsState.Error(throwable)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                GeneralSettingsState.Loading
            )

    /*
     * Modify
     */

    fun toggleAlarm(context: Context, alarm: Alarm) {
        viewModelScope.launch {
            val modifiedAlarm = alarm
                .copy(enabled = !alarm.enabled)
                .run { if (enabled) withFuturizedDateTime() else this }

            alarmRepository.updateAlarm(modifiedAlarm)

            if (modifiedAlarm.enabled) {
                scheduleAlarm(context, modifiedAlarm)
                showSnackbar(SnackbarEvent(modifiedAlarm.toScheduleString(context)))
            } else {
                cancelAndResetAlarm(context, modifiedAlarm)
            }
        }
    }

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        AlarmScheduler.scheduleAlarm(context, alarm.toAlarmExecutionData())
    }

    private suspend fun cancelAndResetAlarm(context: Context, alarm: Alarm) {
        AlarmScheduler.cancelAlarm(context, alarm.toAlarmExecutionData())
        alarmRepository.resetSnooze(alarm.id)
    }

    fun cancelAndDeleteAlarm(context: Context, alarm: Alarm) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, alarm.toAlarmExecutionData())
            alarmRepository.deleteAlarm(alarm)
        }
    }

    /*
     * Snackbar
     */

    private suspend fun showSnackbar(snackbarEvent: SnackbarEvent) {
        GlobalSnackbarController.sendEvent(snackbarEvent)
    }
}

package com.joebsource.lavalarm.alarm.ui.alarmlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joebsource.lavalarm.alarm.alarmexecution.AlarmScheduler
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.repository.AlarmDatabase
import com.joebsource.lavalarm.alarm.data.repository.AlarmListState
import com.joebsource.lavalarm.alarm.data.repository.AlarmRepository
import com.joebsource.lavalarm.core.extension.toAlarmExecutionData
import com.joebsource.lavalarm.core.extension.toScheduleString
import com.joebsource.lavalarm.core.extension.withFuturizedDateTime
import com.joebsource.lavalarm.core.ui.snackbar.SnackbarEvent
import com.joebsource.lavalarm.core.ui.snackbar.global.GlobalSnackbarController
import com.joebsource.lavalarm.settings.data.model.GeneralSettings
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsRepository
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsState
import com.joebsource.lavalarm.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmListViewModel(
    private val alarmRepository: AlarmRepository,
    private val generalSettingsRepository: GeneralSettingsRepository
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

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                AlarmListViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao()),
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore)
                )
            }
        }
    }

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

package com.example.alarmscratch.alarm.ui.alarmlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmSchedulerImpl
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.futurizeDateTime
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmListViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao()),
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore)
                ) as T
            }
        }
    }

    suspend fun toggleAlarm(context: Context, alarm: Alarm) {
        val modifiedAlarm = alarm.copy(enabled = !alarm.enabled, dateTime = alarm.dateTime.futurizeDateTime())

        alarmRepository.updateAlarm(modifiedAlarm)

        if (modifiedAlarm.enabled) {
            scheduleAlarm(context, modifiedAlarm)
        } else {
            cancelAlarm(context, modifiedAlarm)
        }
    }

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        val alarmScheduler = AlarmSchedulerImpl(context)
        alarmScheduler.scheduleAlarm(alarm)
    }

    private fun cancelAlarm(context: Context, alarm: Alarm) {
        val alarmScheduler = AlarmSchedulerImpl(context)
        alarmScheduler.cancelAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        alarmRepository.deleteAlarm(alarm)
    }
}

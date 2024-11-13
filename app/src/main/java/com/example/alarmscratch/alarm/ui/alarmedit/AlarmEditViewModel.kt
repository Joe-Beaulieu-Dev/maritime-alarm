package com.example.alarmscratch.alarm.ui.alarmedit

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import com.example.alarmscratch.alarm.alarmexecution.AlarmScheduler
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.futurizeDateTime
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.nextRepeatingDate
import com.example.alarmscratch.core.extension.toAlarmExecutionData
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val alarmRepository: AlarmRepository,
    private val generalSettingsRepository: GeneralSettingsRepository
) : ViewModel() {

    // Alarm
    private val alarmId: Int = savedStateHandle.toRoute<Destination.AlarmEditScreen>().alarmId
    private val _modifiedAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
    val modifiedAlarm: StateFlow<AlarmState> = _modifiedAlarm.asStateFlow()
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

    init {
        viewModelScope.launch {
            alarmRepository
                .getAlarmFlow(alarmId)
                .map<Alarm, AlarmState> { alarm -> AlarmState.Success(alarm) }
                .catch { throwable -> emit(AlarmState.Error(throwable)) }
                .collect { alarmState -> _modifiedAlarm.value = alarmState }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmEditViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao()),
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore)
                ) as T
            }
        }
    }

    fun saveAndScheduleAlarm(context: Context) {
        viewModelScope.launch {
            if (_modifiedAlarm.value is AlarmState.Success) {
                saveAlarm()
                val newAlarm = getAlarm(alarmId)
                // TODO: Only schedule alarm if enabled
                scheduleAlarm(context.applicationContext, newAlarm)
            }
        }
    }

    // TODO: Clear snooze data
    private suspend fun saveAlarm() {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            if (alarm.isRepeating()) {
                alarmRepository.updateAlarm(alarm.copy(dateTime = alarm.nextRepeatingDate()))
            } else {
                alarmRepository.updateAlarm(alarm)
            }
        }
    }

    private suspend fun getAlarm(alarmId: Int): Alarm =
        alarmRepository.getAlarm(alarmId)

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        AlarmScheduler.scheduleAlarm(context, alarm.toAlarmExecutionData())
    }

    fun updateName(name: String) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(name = name))
        }
    }

    fun updateDate(date: LocalDate) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(
                alarm.copy(dateTime = alarm.dateTime.withYear(date.year).withDayOfYear(date.dayOfYear))
            )
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(
                alarm.copy(dateTime = alarm.dateTime.withHour(hour).withMinute(minute).futurizeDateTime())
            )
        }
    }

    fun addDay(day: WeeklyRepeater.Day) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.addDay(day)))
        }
    }

    fun removeDay(day: WeeklyRepeater.Day) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.removeDay(day)))
        }
    }

    fun updateRingtone(ringtoneUriString: String?) {
        if (
            _modifiedAlarm.value is AlarmState.Success &&
            ringtoneUriString != null &&
            ringtoneUriString != RingtoneData.NO_RINGTONE_URI
        ) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(ringtoneUriString = ringtoneUriString))
        }
    }

    fun toggleVibration() {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(isVibrationEnabled = !alarm.isVibrationEnabled))
        }
    }

    fun updateSnoozeDuration(snoozeDuration: Int) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(snoozeDuration = snoozeDuration))
        }
    }

    fun validateAlarm(): Boolean =
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            alarm.dateTime.isAfter(LocalDateTimeUtil.nowTruncated())
        } else {
            false
        }
}

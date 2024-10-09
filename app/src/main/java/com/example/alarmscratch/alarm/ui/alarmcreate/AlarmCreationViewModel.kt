package com.example.alarmscratch.alarm.ui.alarmcreate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmSchedulerImpl
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
import com.example.alarmscratch.settings.data.model.AlarmDefaults
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsState
import com.example.alarmscratch.settings.data.repository.alarmDefaultsDataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmCreationViewModel(
    private val alarmDefaultsRepository: AlarmDefaultsRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val alarmDefaults: MutableStateFlow<AlarmDefaultsState> = MutableStateFlow(AlarmDefaultsState.Loading)
    private val _newAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
    val newAlarm: StateFlow<AlarmState> = _newAlarm.asStateFlow()

    init {
        viewModelScope.launch {
            alarmDefaultsRepository.alarmDefaultsFlow
                .map<AlarmDefaults, AlarmDefaultsState> { alarmDefaults -> AlarmDefaultsState.Success(alarmDefaults) }
                .catch { throwable -> emit(AlarmDefaultsState.Error(throwable)) }
                .collect { alarmDefaultsState ->
                    // Get AlarmDefaultsState
                    alarmDefaults.value = alarmDefaultsState

                    // Create new Alarm with AlarmDefaults
                    if (alarmDefaults.value is AlarmDefaultsState.Success) {
                        val alarmDefaults = (alarmDefaults.value as AlarmDefaultsState.Success).alarmDefaults
                        _newAlarm.value = AlarmState.Success(
                            Alarm(
                                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                                ringtoneUriString = alarmDefaults.ringtoneUri,
                                isVibrationEnabled = alarmDefaults.isVibrationEnabled
                            )
                        )
                    }
                }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmCreationViewModel(
                    alarmDefaultsRepository = AlarmDefaultsRepository(application.alarmDefaultsDataStore),
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                ) as T
            }
        }
    }

    fun saveAndScheduleAlarm(context: Context) {
        viewModelScope.launch {
            try {
                if (_newAlarm.value is AlarmState.Success) {
                    val newAlarmId = async { saveAlarm() }.await()
                    val newAlarm = async { getAlarm(newAlarmId.toInt()) }.await()
                    // TODO: Only schedule alarm if enabled. It should always be enabled here, but it's good practice to check anyways.
                    scheduleAlarm(context.applicationContext, newAlarm)
                }
            } catch (e: Exception) {
                // toInt() can throw an Exception, but it shouldn't. Just catch here to prevent a crash.
            }
        }
    }

    private suspend fun saveAlarm(): Long =
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            if (alarm.isRepeating()) {
                alarmRepository.insertAlarm(alarm.copy(dateTime = alarm.nextRepeatingDate()))
            } else {
                alarmRepository.insertAlarm(alarm)
            }
        } else {
            AlarmActionReceiver.ALARM_NO_ID.toLong()
        }

    private suspend fun getAlarm(alarmId: Int): Alarm =
        alarmRepository.getAlarm(alarmId)

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        AlarmSchedulerImpl(context).scheduleAlarm(alarm)
    }

    fun updateName(name: String) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(name = name))
        }
    }

    fun updateDate(date: LocalDate) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(dateTime = alarm.dateTime.withDayOfYear(date.dayOfYear)))
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(
                alarm.copy(dateTime = alarm.dateTime.withHour(hour).withMinute(minute).futurizeDateTime())
            )
        }
    }

    fun addDay(day: WeeklyRepeater.Day) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.addDay(day)))
        }
    }

    fun removeDay(day: WeeklyRepeater.Day) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.removeDay(day)))
        }
    }

    fun updateRingtone(ringtoneUriString: String?) {
        if (
            _newAlarm.value is AlarmState.Success &&
            ringtoneUriString != null &&
            ringtoneUriString != RingtoneData.NO_RINGTONE_URI
        ) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(ringtoneUriString = ringtoneUriString))
        }
    }

    fun toggleVibration() {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(isVibrationEnabled = !alarm.isVibrationEnabled))
        }
    }

    fun validateAlarm(): Boolean =
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            alarm.dateTime.isAfter(LocalDateTimeUtil.nowTruncated())
        } else {
            false
        }
}

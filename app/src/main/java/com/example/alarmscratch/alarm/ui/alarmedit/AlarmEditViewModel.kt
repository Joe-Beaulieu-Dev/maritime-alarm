package com.example.alarmscratch.alarm.ui.alarmedit

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmSchedulerImpl
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.futurizeDateTime
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.nextRepeatingDate
import com.example.alarmscratch.core.navigation.AlarmEditScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val alarmId: Int = savedStateHandle[AlarmEditScreen.alarmIdArg] ?: -1
    private val _modifiedAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
    val modifiedAlarm: StateFlow<AlarmState> = _modifiedAlarm.asStateFlow()

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
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                ) as T
            }
        }
    }

    suspend fun saveAlarm() {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarm = (_modifiedAlarm.value as AlarmState.Success).alarm
            if (alarm.isRepeating()) {
                alarmRepository.updateAlarm(alarm.copy(dateTime = alarm.nextRepeatingDate()))
            } else {
                alarmRepository.updateAlarm(alarm)
            }
        }
    }

    fun scheduleAlarm(context: Context) {
        if (_modifiedAlarm.value is AlarmState.Success) {
            val alarmScheduler = AlarmSchedulerImpl(context)
            alarmScheduler.scheduleAlarm((_modifiedAlarm.value as AlarmState.Success).alarm)
        }
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
            _modifiedAlarm.value = AlarmState.Success(alarm.copy(dateTime = alarm.dateTime.withDayOfYear(date.dayOfYear)))
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
}

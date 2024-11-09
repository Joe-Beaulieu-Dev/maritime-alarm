package com.example.alarmscratch.core.ui.core.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isSnoozed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NextAlarmCloudViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    val nextAlarm: StateFlow<AlarmState> =
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
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                AlarmState.Loading
            )

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return NextAlarmCloudViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                ) as T
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
}

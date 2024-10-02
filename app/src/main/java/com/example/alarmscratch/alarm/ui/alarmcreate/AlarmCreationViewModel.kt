package com.example.alarmscratch.alarm.ui.alarmcreate

import android.content.Context
import android.media.RingtoneManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmSchedulerImpl
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.futurizeDateTime
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.nextRepeatingDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class AlarmCreationViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    private val _newAlarm = MutableStateFlow(
        Alarm(
            dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
            ringtoneUriString = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: RingtoneData.NO_RINGTONE_URI
        )
    )
    val newAlarm: StateFlow<Alarm> = _newAlarm.asStateFlow()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmCreationViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                ) as T
            }
        }
    }

    suspend fun saveAlarm() {
        if (_newAlarm.value.isRepeating()) {
            alarmRepository.insertAlarm(_newAlarm.value.copy(dateTime = _newAlarm.value.nextRepeatingDate()))
        } else {
            alarmRepository.insertAlarm(_newAlarm.value)
        }
    }

    fun scheduleAlarm(context: Context) {
        val alarmScheduler = AlarmSchedulerImpl(context)
        alarmScheduler.scheduleAlarm(_newAlarm.value)
    }

    fun updateName(name: String) {
        _newAlarm.value = _newAlarm.value.copy(name = name)
    }

    fun updateDate(date: LocalDate) {
        _newAlarm.value = _newAlarm.value.copy(dateTime = _newAlarm.value.dateTime.withDayOfYear(date.dayOfYear))
    }

    fun updateTime(hour: Int, minute: Int) {
        _newAlarm.value = _newAlarm.value.copy(
            dateTime = _newAlarm.value.dateTime.withHour(hour).withMinute(minute).futurizeDateTime()
        )
    }

    fun addDay(day: WeeklyRepeater.Day) {
        _newAlarm.value = _newAlarm.value.copy(weeklyRepeater = _newAlarm.value.weeklyRepeater.addDay(day))
    }

    fun removeDay(day: WeeklyRepeater.Day) {
        _newAlarm.value = _newAlarm.value.copy(weeklyRepeater = _newAlarm.value.weeklyRepeater.removeDay(day))
    }

    fun updateRingtone(ringtoneUriString: String?) {
        if (ringtoneUriString != null && ringtoneUriString != RingtoneData.NO_RINGTONE_URI) {
            _newAlarm.value = _newAlarm.value.copy(ringtoneUriString = ringtoneUriString)
        }
    }

    fun validateAlarm(): Boolean = _newAlarm.value.dateTime.isAfter(LocalDateTimeUtil.nowTruncated())
}

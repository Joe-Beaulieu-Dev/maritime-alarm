package com.example.alarmscratch.ui.alarmcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.model.WeeklyRepeater
import com.example.alarmscratch.data.repository.AlarmDatabase
import com.example.alarmscratch.data.repository.AlarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class AlarmCreationViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    private val _newAlarm = MutableStateFlow(Alarm(dateTime = LocalDateTime.now().withNano(0).plusHours(1)))
    val newAlarm: StateFlow<Alarm> = _newAlarm

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
        alarmRepository.insertAlarm(_newAlarm.value)
    }

    fun updateName(name: String) {
        _newAlarm.value = _newAlarm.value.copy(name = name)
    }

    fun updateTime(hour: Int, minute: Int) {
        _newAlarm.value = _newAlarm.value.copy(dateTime = _newAlarm.value.dateTime.withHour(hour).withMinute(minute))
    }

    fun addDay(day: WeeklyRepeater.Day) {
        _newAlarm.value.weeklyRepeater.addDay(day)
    }

    fun removeDay(day: WeeklyRepeater.Day) {
        _newAlarm.value.weeklyRepeater.removeDay(day)
    }
}

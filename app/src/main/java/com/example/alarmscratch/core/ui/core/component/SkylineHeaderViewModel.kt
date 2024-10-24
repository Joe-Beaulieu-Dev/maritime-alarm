package com.example.alarmscratch.core.ui.core.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SkylineHeaderViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    val alarmList: StateFlow<AlarmListState> =
        alarmRepository
            .getAllAlarmsFlow()
            .map<List<Alarm>, AlarmListState> { alarmList -> AlarmListState.Success(alarmList) }
            .catch { throwable -> emit(AlarmListState.Error(throwable)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                AlarmListState.Loading
            )

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return SkylineHeaderViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao())
                ) as T
            }
        }
    }
}

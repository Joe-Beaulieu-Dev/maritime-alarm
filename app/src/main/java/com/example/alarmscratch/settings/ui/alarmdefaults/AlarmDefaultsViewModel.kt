package com.example.alarmscratch.settings.ui.alarmdefaults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.settings.data.model.AlarmDefaults
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsState
import com.example.alarmscratch.settings.data.repository.alarmDefaultsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AlarmDefaultsViewModel(private val alarmDefaultsRepository: AlarmDefaultsRepository) : ViewModel() {

    private val _modifiedAlarmDefaults: MutableStateFlow<AlarmDefaultsState> = MutableStateFlow(AlarmDefaultsState.Loading)
    val modifiedAlarmDefaults: StateFlow<AlarmDefaultsState> = _modifiedAlarmDefaults.asStateFlow()

    init {
        viewModelScope.launch {
            alarmDefaultsRepository.alarmDefaultsFlow
                .map<AlarmDefaults, AlarmDefaultsState> { alarmDefaults -> AlarmDefaultsState.Success(alarmDefaults) }
                .catch { throwable -> emit(AlarmDefaultsState.Error(throwable)) }
                .collect { alarmDefaultsState -> _modifiedAlarmDefaults.value = alarmDefaultsState }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmDefaultsViewModel(
                    alarmDefaultsRepository = AlarmDefaultsRepository(application.alarmDefaultsDataStore)
                ) as T
            }
        }
    }

    suspend fun saveAlarmDefaults() {
        if (_modifiedAlarmDefaults.value is AlarmDefaultsState.Success) {
            val alarmDefaults = (_modifiedAlarmDefaults.value as AlarmDefaultsState.Success).alarmDefaults
            alarmDefaultsRepository.updateAlarmDefaults(alarmDefaults)
        }
    }

    fun updateRingtone(ringtoneUri: String?) {
        if (
            _modifiedAlarmDefaults.value is AlarmDefaultsState.Success &&
            ringtoneUri != null &&
            ringtoneUri != RingtoneData.NO_RINGTONE_URI
        ) {
            val alarmDefaults = (_modifiedAlarmDefaults.value as AlarmDefaultsState.Success).alarmDefaults
            _modifiedAlarmDefaults.value = AlarmDefaultsState.Success(alarmDefaults.copy(ringtoneUri = ringtoneUri))
        }
    }

    fun toggleVibration() {
        if (_modifiedAlarmDefaults.value is AlarmDefaultsState.Success) {
            val alarmDefaults = (_modifiedAlarmDefaults.value as AlarmDefaultsState.Success).alarmDefaults
            _modifiedAlarmDefaults.value = AlarmDefaultsState.Success(
                alarmDefaults.copy(isVibrationEnabled = !alarmDefaults.isVibrationEnabled)
            )
        }
    }
}

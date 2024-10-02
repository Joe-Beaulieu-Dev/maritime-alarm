package com.example.alarmscratch.settings.data.repository

import com.example.alarmscratch.settings.data.model.AlarmDefaults

sealed interface AlarmDefaultsState {
    data object Loading : AlarmDefaultsState
    data class Success(val alarmDefaults: AlarmDefaults) : AlarmDefaultsState
    data class Error(val throwable: Throwable) : AlarmDefaultsState
}

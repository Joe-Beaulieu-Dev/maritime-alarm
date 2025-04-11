package com.joebsource.lavalarm.settings.data.repository

import com.joebsource.lavalarm.settings.data.model.AlarmDefaults

sealed interface AlarmDefaultsState {
    data object Loading : AlarmDefaultsState
    data class Success(val alarmDefaults: AlarmDefaults) : AlarmDefaultsState
    data class Error(val throwable: Throwable) : AlarmDefaultsState
}

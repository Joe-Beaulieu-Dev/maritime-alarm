package com.octrobi.lavalarm.alarm.data.repository

import com.octrobi.lavalarm.alarm.data.model.Alarm

sealed interface AlarmState {
    data object Loading : AlarmState
    data class Success(val alarm: Alarm) : AlarmState
    data class Error(val throwable: Throwable) : AlarmState
}

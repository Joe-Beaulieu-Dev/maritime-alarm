package com.example.alarmscratch.data.repository

import com.example.alarmscratch.data.model.Alarm

sealed interface AlarmState {
    data object Loading : AlarmState
    data class Success(val alarm: Alarm) : AlarmState
    data class Error(val throwable: Throwable) : AlarmState
}

package com.example.alarmscratch.alarm.data.repository

import com.example.alarmscratch.alarm.data.model.Alarm

sealed interface AlarmListState {
    data object Loading : AlarmListState
    data class Success(val alarmList: List<Alarm>) : AlarmListState
    data class Error(val throwable: Throwable) : AlarmListState
}

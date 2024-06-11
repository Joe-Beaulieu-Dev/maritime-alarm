package com.example.alarmscratch.data.repository

import com.example.alarmscratch.data.model.Alarm

sealed interface AlarmListState {
    data object Loading : AlarmListState
    data class Success(val alarmList: List<Alarm>) : AlarmListState
    data class Error(val throwable: Throwable) : AlarmListState
}

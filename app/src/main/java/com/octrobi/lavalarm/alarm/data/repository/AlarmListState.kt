package com.octrobi.lavalarm.alarm.data.repository

import com.octrobi.lavalarm.alarm.data.model.Alarm

sealed interface AlarmListState {
    data object Loading : AlarmListState
    data class Success(val alarmList: List<Alarm>) : AlarmListState
    data class Error(val throwable: Throwable) : AlarmListState
}

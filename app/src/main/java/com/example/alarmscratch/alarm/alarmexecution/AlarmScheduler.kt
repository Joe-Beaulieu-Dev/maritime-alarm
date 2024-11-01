package com.example.alarmscratch.alarm.alarmexecution

import com.example.alarmscratch.alarm.data.model.AlarmExecutionData

interface AlarmScheduler {
    fun scheduleInitialAlarm(alarmExecutionData: AlarmExecutionData)
    fun scheduleSnoozedAlarm(alarmExecutionData: AlarmExecutionData)
    fun cancelAlarm(alarmExecutionData: AlarmExecutionData)
}

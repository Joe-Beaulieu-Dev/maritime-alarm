package com.example.alarmscratch.alarm.alarmexecution

import com.example.alarmscratch.alarm.data.model.Alarm

interface AlarmScheduler {
    fun scheduleInitialAlarm(alarm: Alarm)
    fun scheduleSnoozedAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
}

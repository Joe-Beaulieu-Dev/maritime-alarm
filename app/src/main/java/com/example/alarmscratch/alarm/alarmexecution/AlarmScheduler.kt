package com.example.alarmscratch.alarm.alarmexecution

import com.example.alarmscratch.alarm.data.model.Alarm

interface AlarmScheduler {
    fun scheduleAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
}

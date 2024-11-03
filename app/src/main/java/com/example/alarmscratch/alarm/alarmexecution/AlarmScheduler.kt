package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import com.example.alarmscratch.alarm.data.model.AlarmExecutionData

interface AlarmScheduler {
    fun scheduleAlarm(context: Context, alarmExecutionData: AlarmExecutionData)
    fun cancelAlarm(context: Context, alarmExecutionData: AlarmExecutionData)
}

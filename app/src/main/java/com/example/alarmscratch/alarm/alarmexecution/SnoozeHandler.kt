package com.example.alarmscratch.alarm.alarmexecution

import android.content.Context
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.core.extension.LocalDateTimeUtil

object SnoozeHandler {

    suspend fun snoozeAlarm(context: Context, alarm: Alarm) {
        val alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(context.applicationContext).alarmDao())
        val newSnoozeDateTime = LocalDateTimeUtil.nowTruncated().plusMinutes(alarm.snoozeDuration.toLong())

        alarmRepository.updateAlarm(alarm.copy(snoozeDateTime = newSnoozeDateTime))
    }
}

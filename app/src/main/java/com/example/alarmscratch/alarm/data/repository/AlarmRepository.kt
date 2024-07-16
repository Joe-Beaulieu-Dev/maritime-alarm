package com.example.alarmscratch.alarm.data.repository

import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmDao
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {

    suspend fun insertAlarm(alarm: Alarm) = alarmDao.insert(alarm = alarm)

    suspend fun updateAlarm(alarm: Alarm) = alarmDao.update(alarm = alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.delete(alarm = alarm)

    fun getAlarmFlow(id: Int): Flow<Alarm> = alarmDao.getAlarmFlow(id = id)

    fun getAllAlarmsFlow(): Flow<List<Alarm>> = alarmDao.getAllAlarmsFlow()
}

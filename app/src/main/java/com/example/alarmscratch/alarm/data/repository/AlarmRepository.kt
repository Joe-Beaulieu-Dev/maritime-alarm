package com.example.alarmscratch.alarm.data.repository

import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class AlarmRepository(private val alarmDao: AlarmDao) {

    /*
     *******************************
     * Coarse-grained Transactions *
     *******************************
     */

    /*
     * One-shot Read/Write
     */
    suspend fun insertAlarm(alarm: Alarm) = alarmDao.insert(alarm = alarm)

    suspend fun updateAlarm(alarm: Alarm) = alarmDao.update(alarm = alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.delete(alarm = alarm)

    suspend fun getAlarm(id: Int): Alarm = alarmDao.getAlarm(id = id)

    suspend fun getAllAlarms(): List<Alarm> = alarmDao.getAllAlarms()

    /*
     * Observable Read
     */
    fun getAlarmFlow(id: Int): Flow<Alarm> = alarmDao.getAlarmFlow(id = id)

    fun getAllAlarmsFlow(): Flow<List<Alarm>> = alarmDao.getAllAlarmsFlow()

    /*
     *****************************
     * Fine-grained Transactions *
     *****************************
     */

    /*
     * One-shot Read/Write
     */
    suspend fun updateSnoozeDateTime(id: Int, snoozeDateTime: LocalDateTime) =
        alarmDao.updateSnoozeDateTime(id = id, snoozeDateTime = snoozeDateTime)

    suspend fun dismissAlarm(id: Int) = alarmDao.dismissAlarm(id = id)
}

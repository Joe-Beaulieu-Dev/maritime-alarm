package com.joebsource.lavalarm.alarm.data.repository

import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.AlarmDao
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
    suspend fun getAllEnabledAlarms(): List<Alarm> = alarmDao.getAllEnabledAlarms()

    suspend fun updateSnooze(id: Int, snoozeDateTime: LocalDateTime) =
        alarmDao.updateSnooze(id = id, snoozeDateTime = snoozeDateTime)

    suspend fun resetSnooze(id: Int) = alarmDao.resetSnooze(id = id)

    suspend fun dismissAlarm(id: Int) = alarmDao.dismissAlarm(id = id)

    suspend fun dismissAndRescheduleRepeating(id: Int, dateTime: LocalDateTime) =
        alarmDao.dismissAndRescheduleRepeating(id = id, dateTime = dateTime)
}

package com.example.alarmscratch.alarm.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface AlarmDao {

    /*
     *******************************
     * Coarse-grained Transactions *
     *******************************
     */

    /*
     * One-shot Read/Write
     */
    // TODO: Think about onConflict param more
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alarm: Alarm): Long

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarm(id: Int): Alarm

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<Alarm>

    /*
     * Observable Read
     */
    @Query("SELECT * FROM alarms WHERE id = :id")
    fun getAlarmFlow(id: Int): Flow<Alarm>

    @Query("SELECT * FROM alarms")
    fun getAllAlarmsFlow(): Flow<List<Alarm>>

    /*
     *****************************
     * Fine-grained Transactions *
     *****************************
     */

    /*
     * One-shot Read/Write
     */
    @Query("UPDATE alarms SET snoozeDateTime = :snoozeDateTime WHERE id = :id")
    suspend fun updateSnooze(id: Int, snoozeDateTime: LocalDateTime)

    @Query("UPDATE alarms SET snoozeDateTime = null WHERE id = :id")
    suspend fun resetSnooze(id: Int)

    @Query("UPDATE alarms SET enabled = 0, snoozeDateTime = null WHERE id = :id")
    suspend fun dismissAlarm(id: Int)

    @Query("UPDATE alarms SET dateTime = :dateTime, snoozeDateTime = null WHERE id = :id")
    suspend fun dismissAndRescheduleRepeating(id: Int, dateTime: LocalDateTime)
}

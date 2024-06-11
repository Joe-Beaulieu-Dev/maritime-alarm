package com.example.alarmscratch.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    // TODO: Think about onConflict param more
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alarm: Alarm)

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * from alarms WHERE id = :id")
    fun getAlarmFlow(id: Int): Flow<Alarm>

    @Query("SELECT * from alarms")
    fun getAllAlarmsFlow(): Flow<List<Alarm>>
}

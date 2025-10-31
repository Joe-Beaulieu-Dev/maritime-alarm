package com.octrobi.lavalarm.alarm.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.AlarmDao
import com.octrobi.lavalarm.alarm.data.model.LocalDateTimeConverter
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeaterConverter

@Database(entities = [Alarm::class], version = 1)
@TypeConverters(value = [LocalDateTimeConverter::class, WeeklyRepeaterConverter::class])
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
}

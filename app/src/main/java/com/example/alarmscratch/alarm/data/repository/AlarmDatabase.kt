package com.example.alarmscratch.alarm.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.AlarmDao
import com.example.alarmscratch.alarm.data.model.LocalDateTimeConverter
import com.example.alarmscratch.alarm.data.model.WeeklyRepeaterConverter
import kotlin.concurrent.Volatile

// TODO: Export Schema
@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@TypeConverters(value = [LocalDateTimeConverter::class, WeeklyRepeaterConverter::class])
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {

        @Volatile
        private var Instance: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase =
            Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AlarmDatabase::class.java, "alarm_database")
                    // TODO: Remove destructive fallback. Just doing this for now.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { databaseInstance -> Instance = databaseInstance }
            }
    }
}

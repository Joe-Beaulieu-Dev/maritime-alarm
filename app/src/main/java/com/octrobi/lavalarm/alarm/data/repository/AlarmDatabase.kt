package com.octrobi.lavalarm.alarm.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {

        @Volatile
        private var Instance: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase =
            Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.createDeviceProtectedStorageContext(),
                    AlarmDatabase::class.java,
                    "alarm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { databaseInstance -> Instance = databaseInstance }
            }
    }
}

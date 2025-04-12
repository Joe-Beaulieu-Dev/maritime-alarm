package com.joebsource.lavalarm.alarm.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.AlarmDao
import com.joebsource.lavalarm.alarm.data.model.LocalDateTimeConverter
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeaterConverter
import kotlin.concurrent.Volatile

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

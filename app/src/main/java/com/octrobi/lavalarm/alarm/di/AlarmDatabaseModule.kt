package com.octrobi.lavalarm.alarm.di

import android.content.Context
import androidx.room.Room
import com.octrobi.lavalarm.alarm.data.model.AlarmDao
import com.octrobi.lavalarm.alarm.data.repository.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmDatabaseModule {

    @Provides
    fun provideAlarmDao(alarmDatabase: AlarmDatabase): AlarmDao =
        alarmDatabase.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext applicationContext: Context): AlarmDatabase =
        Room.databaseBuilder(
            applicationContext.createDeviceProtectedStorageContext(),
            AlarmDatabase::class.java,
            "alarm_database"
        )
            .fallbackToDestructiveMigration()
            .build()
}

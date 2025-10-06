package com.octrobi.lavalarm.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.octrobi.lavalarm.settings.data.repository.AlarmDefaultsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class AlarmDefaultsDataStore

@Module
@InstallIn(SingletonComponent::class)
object AlarmDefaultsDataStoreModule {

    @Provides
    @Singleton
    @AlarmDefaultsDataStore
    fun provideAlarmDefaultsDataStore(@ApplicationContext applicationContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = {
                applicationContext.preferencesDataStoreFile(AlarmDefaultsRepository.ALARM_DEFAULTS_PREFERENCES_NAME)
            }
        )
}

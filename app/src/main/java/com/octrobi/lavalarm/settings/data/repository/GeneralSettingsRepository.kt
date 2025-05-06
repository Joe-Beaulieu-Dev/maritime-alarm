package com.octrobi.lavalarm.settings.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.octrobi.lavalarm.settings.data.model.GeneralSettings
import com.octrobi.lavalarm.settings.data.model.TimeDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.generalSettingsDataStore by preferencesDataStore(
    name = GeneralSettingsRepository.GENERAL_SETTINGS_PREFERENCES_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class GeneralSettingsRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        // Preferences name
        const val GENERAL_SETTINGS_PREFERENCES_NAME = "general_settings"

        // Keys
        private val KEY_TIME_DISPLAY = stringPreferencesKey("time_display")

        // Default values
        val DEFAULT_TIME_DISPLAY = TimeDisplay.TwelveHour
    }

    val generalSettingsFlow: Flow<GeneralSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            // Get Preferences
            val timeDisplay = TimeDisplay.fromString(preferences[KEY_TIME_DISPLAY]) ?: DEFAULT_TIME_DISPLAY

            // Return GeneralSettings
            GeneralSettings(timeDisplay)
        }

    suspend fun updateGeneralSettings(generalSettings: GeneralSettings) {
        dataStore.edit { preferences ->
            preferences[KEY_TIME_DISPLAY] = generalSettings.timeDisplay.value
        }
    }
}

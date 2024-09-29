package com.example.alarmscratch.settings.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

const val ALARM_DEFAULTS_PREFERENCES_NAME = "alarm_defaults_preferences_name"

val Context.alarmDefaultsDataStore by preferencesDataStore(
    name = ALARM_DEFAULTS_PREFERENCES_NAME,
    // TODO: Place default preferences here
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

data class AlarmDefaults(
    val ringtoneUri: String,
    val isVibrationEnabled: Boolean
)

private object PreferencesKeys {
    val RINGTONE_URI = stringPreferencesKey("ringtone_uri")
    val IS_VIBRATION_ENABLED = booleanPreferencesKey("is_vibration_enabled")
}

class AlarmDefaultsRepository(private val dataStore: DataStore<Preferences>) {

    val alarmDefaultsFlow: Flow<AlarmDefaults> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            // TODO: Change these defaults
            // Get preferences
            val ringtoneUri = preferences[PreferencesKeys.RINGTONE_URI] ?: ""
            val isVibrationEnabled = preferences[PreferencesKeys.IS_VIBRATION_ENABLED] ?: false

            // Return AlarmDefaults
            AlarmDefaults(ringtoneUri, isVibrationEnabled)
        }

    suspend fun updateDefaultRingtoneUri(defaultRingtoneUri: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RINGTONE_URI] = defaultRingtoneUri
        }
    }

    suspend fun updateVibration(vibrationDefault: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_VIBRATION_ENABLED] = vibrationDefault
        }
    }
}

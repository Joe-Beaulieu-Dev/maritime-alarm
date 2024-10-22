package com.example.alarmscratch.settings.data.repository

import android.content.Context
import android.media.RingtoneManager
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.settings.data.model.AlarmDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.alarmDefaultsDataStore by preferencesDataStore(
    name = AlarmDefaultsRepository.ALARM_DEFAULTS_PREFERENCES_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class AlarmDefaultsRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        // Preferences name
        const val ALARM_DEFAULTS_PREFERENCES_NAME = "alarm_defaults_preferences_name"

        // Keys
        private val KEY_RINGTONE_URI = stringPreferencesKey("ringtone_uri")
        private val KEY_IS_VIBRATION_ENABLED = booleanPreferencesKey("is_vibration_enabled")
        private val KEY_SNOOZE_DURATION = intPreferencesKey("snooze_duration")

        // Default values
        private val DEFAULT_RINGTONE_URI =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: RingtoneData.NO_RINGTONE_URI
        private const val DEFAULT_IS_VIBRATION_ENABLED = false
        private const val DEFAULT_SNOOZE_DURATION = 10
    }

    val alarmDefaultsFlow: Flow<AlarmDefaults> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            // TODO: Exception handling
            // Get Preferences
            val ringtoneUri = preferences[KEY_RINGTONE_URI] ?: DEFAULT_RINGTONE_URI
            val isVibrationEnabled = preferences[KEY_IS_VIBRATION_ENABLED] ?: DEFAULT_IS_VIBRATION_ENABLED
            val snoozeDuration = preferences[KEY_SNOOZE_DURATION] ?: DEFAULT_SNOOZE_DURATION

            // Return AlarmDefaults
            AlarmDefaults(ringtoneUri, isVibrationEnabled, snoozeDuration)
        }

    suspend fun updateAlarmDefaults(alarmDefaults: AlarmDefaults) {
        // TODO: Exception handling
        dataStore.edit { preferences ->
            preferences[KEY_RINGTONE_URI] = alarmDefaults.ringtoneUri
            preferences[KEY_IS_VIBRATION_ENABLED] = alarmDefaults.isVibrationEnabled
            preferences[KEY_SNOOZE_DURATION] = alarmDefaults.snoozeDuration
        }
    }
}

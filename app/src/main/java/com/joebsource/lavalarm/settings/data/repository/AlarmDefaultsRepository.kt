package com.joebsource.lavalarm.settings.data.repository

import android.app.Application
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
import com.joebsource.lavalarm.core.data.model.RingtoneData
import com.joebsource.lavalarm.core.data.repository.RingtoneRepository
import com.joebsource.lavalarm.core.extension.alarmApplication
import com.joebsource.lavalarm.settings.data.model.AlarmDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.alarmDefaultsDataStore by preferencesDataStore(
    name = AlarmDefaultsRepository.ALARM_DEFAULTS_PREFERENCES_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class AlarmDefaultsRepository(
    application: Application,
    private val dataStore: DataStore<Preferences>
) {

    init {
        application.alarmApplication.applicationScope.launch {
            initRingtoneUri(application)
        }
    }

    companion object {
        // Preferences name
        const val ALARM_DEFAULTS_PREFERENCES_NAME = "alarm_defaults"

        // Keys
        private val KEY_RINGTONE_URI = stringPreferencesKey("ringtone_uri")
        private val KEY_IS_VIBRATION_ENABLED = booleanPreferencesKey("is_vibration_enabled")
        private val KEY_SNOOZE_DURATION = intPreferencesKey("snooze_duration")

        // Default values
        private val SYSTEM_DEFAULT_RINGTONE_URI =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: RingtoneData.NO_RINGTONE_URI
        private const val DEFAULT_IS_VIBRATION_ENABLED = false
        const val DEFAULT_SNOOZE_DURATION = 10
    }

    /*
     * Initialization
     */

    private suspend fun initRingtoneUri(context: Context) {
        val preferences = dataStore.data.catch { emit(emptyPreferences()) }.firstOrNull()
        if (preferences != null) {
            val ringtoneUri = preferences[KEY_RINGTONE_URI]
            // Ringtone URI is not set. Initialize it.
            if (ringtoneUri == null) {
                dataStore.edit {
                    it[KEY_RINGTONE_URI] = RingtoneRepository(context).tryGetNonGenericSystemDefaultUri()
                }
            }
        }
    }

    /*
     * Read
     */

    val alarmDefaultsFlow: Flow<AlarmDefaults> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            // Get Preferences
            val ringtoneUri = preferences[KEY_RINGTONE_URI] ?: SYSTEM_DEFAULT_RINGTONE_URI
            val isVibrationEnabled = preferences[KEY_IS_VIBRATION_ENABLED] ?: DEFAULT_IS_VIBRATION_ENABLED
            val snoozeDuration = preferences[KEY_SNOOZE_DURATION] ?: DEFAULT_SNOOZE_DURATION

            // Return AlarmDefaults
            AlarmDefaults(ringtoneUri, isVibrationEnabled, snoozeDuration)
        }

    /*
     * Write
     */

    suspend fun updateAlarmDefaults(alarmDefaults: AlarmDefaults) {
        dataStore.edit { preferences ->
            preferences[KEY_RINGTONE_URI] = alarmDefaults.ringtoneUri
            preferences[KEY_IS_VIBRATION_ENABLED] = alarmDefaults.isVibrationEnabled
            preferences[KEY_SNOOZE_DURATION] = alarmDefaults.snoozeDuration
        }
    }
}

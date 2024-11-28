package com.example.alarmscratch.alarm.ui.alarmcreate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.alarmscratch.alarm.alarmexecution.AlarmActionReceiver
import com.example.alarmscratch.alarm.alarmexecution.AlarmScheduler
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.repository.AlarmDatabase
import com.example.alarmscratch.alarm.data.repository.AlarmRepository
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.alarm.validation.AlarmValidator
import com.example.alarmscratch.alarm.validation.ValidationResult
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.isRepeating
import com.example.alarmscratch.core.extension.nextRepeatingDateTime
import com.example.alarmscratch.core.extension.toAlarmExecutionData
import com.example.alarmscratch.core.extension.withFuturizedDateTime
import com.example.alarmscratch.settings.data.model.AlarmDefaults
import com.example.alarmscratch.settings.data.model.GeneralSettings
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsRepository
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsState
import com.example.alarmscratch.settings.data.repository.GeneralSettingsRepository
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import com.example.alarmscratch.settings.data.repository.alarmDefaultsDataStore
import com.example.alarmscratch.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AlarmCreationViewModel(
    private val alarmRepository: AlarmRepository,
    private val alarmDefaultsRepository: AlarmDefaultsRepository,
    private val generalSettingsRepository: GeneralSettingsRepository,
    private val alarmValidator: AlarmValidator
) : ViewModel() {

    // Alarm
    private val _newAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
    val newAlarm: StateFlow<AlarmState> = _newAlarm.asStateFlow()

    // Settings
    private val alarmDefaults: MutableStateFlow<AlarmDefaultsState> = MutableStateFlow(AlarmDefaultsState.Loading)
    val generalSettings: StateFlow<GeneralSettingsState> =
        generalSettingsRepository.generalSettingsFlow
            .map<GeneralSettings, GeneralSettingsState> { generalSettings -> GeneralSettingsState.Success(generalSettings) }
            .catch { throwable -> emit(GeneralSettingsState.Error(throwable)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                GeneralSettingsState.Loading
            )

    // Snackbar
    private val snackbarChannel = Channel<ValidationResult.Error<AlarmValidator.DateTimeError>>()
    val snackbarChannelFlow = snackbarChannel.receiveAsFlow()

    // Validation
    private var isAlarmDateTimeValid: ValidationResult<AlarmValidator.DateTimeError> = ValidationResult.Success()
    private val _isAlarmNameValid: MutableStateFlow<ValidationResult<AlarmValidator.NameError>> =
        MutableStateFlow(ValidationResult.Success())
    val isAlarmNameValid: StateFlow<ValidationResult<AlarmValidator.NameError>> = _isAlarmNameValid.asStateFlow()

    init {
        viewModelScope.launch {
            alarmDefaultsRepository.alarmDefaultsFlow
                .map<AlarmDefaults, AlarmDefaultsState> { alarmDefaults -> AlarmDefaultsState.Success(alarmDefaults) }
                .catch { throwable -> emit(AlarmDefaultsState.Error(throwable)) }
                .collect { alarmDefaultsState ->
                    // Get AlarmDefaultsState
                    alarmDefaults.value = alarmDefaultsState

                    // Create new Alarm with AlarmDefaults
                    if (alarmDefaults.value is AlarmDefaultsState.Success) {
                        val alarmDefaults = (alarmDefaults.value as AlarmDefaultsState.Success).alarmDefaults
                        _newAlarm.value = AlarmState.Success(
                            Alarm(
                                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                                ringtoneUriString = alarmDefaults.ringtoneUri,
                                isVibrationEnabled = alarmDefaults.isVibrationEnabled,
                                snoozeDuration = alarmDefaults.snoozeDuration
                            )
                        )
                    }
                }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // TODO: Do something about this
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                return AlarmCreationViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao()),
                    alarmDefaultsRepository = AlarmDefaultsRepository(application.alarmDefaultsDataStore),
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore),
                    alarmValidator = AlarmValidator()
                ) as T
            }
        }
    }

    fun saveAndScheduleAlarm(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (
                    _newAlarm.value is AlarmState.Success &&
                    validateAlarm()
                ) {
                    val newAlarmId = saveAlarm()
                    val newAlarm = getAlarm(newAlarmId.toInt())
                    scheduleAlarm(context.applicationContext, newAlarm)

                    // Switch back to Main Thread for UI related work
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                // toInt() can throw an Exception, but it shouldn't. Just catch here to prevent a crash.
            }
        }
    }

    private suspend fun saveAlarm(): Long =
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            if (alarm.isRepeating()) {
                alarmRepository.insertAlarm(alarm.copy(dateTime = alarm.nextRepeatingDateTime()))
            } else {
                alarmRepository.insertAlarm(alarm)
            }
        } else {
            AlarmActionReceiver.ALARM_NO_ID.toLong()
        }

    private suspend fun getAlarm(alarmId: Int): Alarm =
        alarmRepository.getAlarm(alarmId)

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        AlarmScheduler.scheduleAlarm(context, alarm.toAlarmExecutionData())
    }

    fun updateName(name: String) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(name = name))

            // Update validation state for TextField
            validateName()
        }
    }

    fun updateDateAndResetWeeklyRepeater(date: LocalDate) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(
                alarm.copy(
                    dateTime = alarm.dateTime.withYear(date.year).withDayOfYear(date.dayOfYear),
                    weeklyRepeater = WeeklyRepeater()
                )
            )
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(
                alarm.copy(dateTime = alarm.dateTime.withHour(hour).withMinute(minute)).withFuturizedDateTime()
            )
        }
    }

    fun addDay(day: WeeklyRepeater.Day) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.withDay(day)))
        }
    }

    fun removeDay(day: WeeklyRepeater.Day) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(weeklyRepeater = alarm.weeklyRepeater.withoutDay(day)))
        }
    }

    fun updateRingtone(ringtoneUriString: String?) {
        if (
            _newAlarm.value is AlarmState.Success &&
            ringtoneUriString != null &&
            ringtoneUriString != RingtoneData.NO_RINGTONE_URI
        ) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(ringtoneUriString = ringtoneUriString))
        }
    }

    fun toggleVibration() {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(isVibrationEnabled = !alarm.isVibrationEnabled))
        }
    }

    fun updateSnoozeDuration(snoozeDuration: Int) {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(snoozeDuration = snoozeDuration))
        }
    }

    /*
     * Validation
     */

    private fun validateAlarm(): Boolean =
        if (_newAlarm.value is AlarmState.Success) {
            // Validate
            validateName()
            validateDateTimeAndPushToSnackbar()

            // Check validation results
            !(_isAlarmNameValid.value is ValidationResult.Error ||
                    isAlarmDateTimeValid is ValidationResult.Error)
        } else {
            false
        }

    private fun validateName() {
        if (_newAlarm.value is AlarmState.Success) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _isAlarmNameValid.value = alarmValidator.validateName(alarm.name)
        }
    }

    private fun validateDateTimeAndPushToSnackbar() {
        if (_newAlarm.value is AlarmState.Success) {
            // Validate
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            isAlarmDateTimeValid = alarmValidator.validateDateTime(alarm.dateTime)

            // Push to Snackbar Channel
            (isAlarmDateTimeValid as? ValidationResult.Error)?.let { pushToSnackbarChannel(it) }
        }
    }

    private fun pushToSnackbarChannel(validationResult: ValidationResult.Error<AlarmValidator.DateTimeError>) {
        viewModelScope.launch {
            try {
                snackbarChannel.send(validationResult)
            } catch (e: Exception) {
                // send() can throw Exceptions. Edge case where there's nothing to be done besides not crash.
            }
        }
    }
}

package com.joebsource.lavalarm.alarm.ui.alarmcreate

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.joebsource.lavalarm.alarm.alarmexecution.AlarmScheduler
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.alarm.data.repository.AlarmDatabase
import com.joebsource.lavalarm.alarm.data.repository.AlarmRepository
import com.joebsource.lavalarm.alarm.data.repository.AlarmState
import com.joebsource.lavalarm.alarm.util.AlarmUtil
import com.joebsource.lavalarm.alarm.validation.AlarmValidator
import com.joebsource.lavalarm.alarm.validation.ValidationError
import com.joebsource.lavalarm.alarm.validation.ValidationResult
import com.joebsource.lavalarm.core.data.model.RingtoneData
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.extension.isRepeating
import com.joebsource.lavalarm.core.extension.toAlarmExecutionData
import com.joebsource.lavalarm.core.extension.toScheduleString
import com.joebsource.lavalarm.core.extension.withFuturizedDateTime
import com.joebsource.lavalarm.core.ui.snackbar.SnackbarEvent
import com.joebsource.lavalarm.settings.data.model.AlarmDefaults
import com.joebsource.lavalarm.settings.data.model.GeneralSettings
import com.joebsource.lavalarm.settings.data.repository.AlarmDefaultsRepository
import com.joebsource.lavalarm.settings.data.repository.AlarmDefaultsState
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsRepository
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsState
import com.joebsource.lavalarm.settings.data.repository.alarmDefaultsDataStore
import com.joebsource.lavalarm.settings.data.repository.generalSettingsDataStore
import kotlinx.coroutines.CancellationException
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
import java.time.LocalDate
import java.time.LocalDateTime

class AlarmCreationViewModel(
    private val alarmRepository: AlarmRepository,
    private val alarmDefaultsRepository: AlarmDefaultsRepository,
    private val generalSettingsRepository: GeneralSettingsRepository,
    private val alarmValidator: AlarmValidator
) : ViewModel() {

    // Alarm
    private val referenceAlarm: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)
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
    private val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    // Dialog
    private val _showUnsavedChangesDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog.asStateFlow()

    // Validation
    private val _isNameLengthValid: MutableStateFlow<ValidationResult<AlarmValidator.NameError>> =
        MutableStateFlow(ValidationResult.Success())
    val isNameLengthValid: StateFlow<ValidationResult<AlarmValidator.NameError>> = _isNameLengthValid.asStateFlow()
    private val _isNameContentValid: MutableStateFlow<ValidationResult<AlarmValidator.NameError>> =
        MutableStateFlow(ValidationResult.Success())
    val isNameContentValid: StateFlow<ValidationResult<AlarmValidator.NameError>> = _isNameContentValid.asStateFlow()
    private var isDateTimeValid: ValidationResult<AlarmValidator.DateTimeError> = ValidationResult.Success()

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
                        val alarmState = AlarmState.Success(
                            Alarm(
                                dateTime = getInitialAlarmDateTime(),
                                ringtoneUri = alarmDefaults.ringtoneUri,
                                isVibrationEnabled = alarmDefaults.isVibrationEnabled,
                                snoozeDuration = alarmDefaults.snoozeDuration
                            )
                        )
                        // Update state
                        referenceAlarm.value = alarmState
                        _newAlarm.value = alarmState
                    }
                }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                AlarmCreationViewModel(
                    alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(application).alarmDao()),
                    alarmDefaultsRepository = AlarmDefaultsRepository(application, application.alarmDefaultsDataStore),
                    generalSettingsRepository = GeneralSettingsRepository(application.generalSettingsDataStore),
                    alarmValidator = AlarmValidator()
                )
            }
        }
    }

    /*
     * Initialization
     */

    private fun getInitialAlarmDateTime(): LocalDateTime {
        val now = LocalDateTimeUtil.nowTruncated()
        val nowMinutes = now.minute

        // Return a LocalDateTime that is rounded up based on the current time
        return LocalDateTimeUtil.nowTruncated().run {
            when (nowMinutes) {
                in 0..20 ->
                    // Round to next half hour
                    withMinute(30)
                in 21..50 ->
                    // Round to next hour
                    plusHours(1).withMinute(0)
                else ->
                    // Round to next hour and a half
                    plusHours(1).withMinute(30)
            }
        }
    }

    /*
     * Save and Schedule
     */

    fun saveAndScheduleAlarm(context: Context, navHostController: NavHostController) {
        if (_newAlarm.value is AlarmState.Success) {
            viewModelScope.launch {
                // Fix edge cases before validation
                val autoCorrectAlarm = preValidationAutoCorrect((_newAlarm.value as AlarmState.Success).alarm)

                // Validate, save, schedule, Snackbar, navigate back
                if (validateAlarm(autoCorrectAlarm)) {
                    // Save and schedule Alarm
                    val newAlarmId = alarmRepository.insertAlarm(autoCorrectAlarm)
                    val newAlarm = alarmRepository.getAlarm(newAlarmId.toInt())
                    scheduleAlarm(context.applicationContext, newAlarm)

                    // Send Snackbar to previous screen and navigate back
                    sendSnackbarToPreviousScreen(navHostController, SnackbarEvent(newAlarm.toScheduleString(context)))
                    navHostController.popBackStack()
                } else {
                    pushTriagedErrorToSnackbar()
                }
            }
        }
    }

    /**
     * Perform auto-correction that should happen to an Alarm before validation.
     *
     * This function is an edge case fixer for repeating Alarms since they should never
     * fail LocalDateTime validation. Since they repeat, rather than failing validation
     * they should just be corrected to execute on the next possible repeating day.
     * This is because a repeating Alarm that displays "Every: Mon, Tue, Wed" failing
     * validation and telling the User to set their Alarm to a time in the future is confusing.
     * The User would reasonably expect the app to just set the Alarm for the next possible
     * repeating date.
     *
     * Non-repeating Alarms should never receive this auto-correction. Instead, the User should
     * be informed that they're trying to set an Alarm for a time in the past.
     *
     * The following is a summary of the corrections that may be applied:
     *
     * Repeating Alarms
     * 1) If the Alarm is set for a time in the past, bump the date to the next repeating date
     * that's in the future.
     *
     * Non-Repeating Alarms: No correction is performed and the Alarm is returned unmodified
     *
     * @param alarm auto-correct candidate
     *
     * @return Alarm that may or may not have been modified according to the above criteria
     */
    private fun preValidationAutoCorrect(alarm: Alarm): Alarm =
        alarm.run {
            if (isRepeating()) {
                copy(dateTime = AlarmUtil.nextRepeatingDateTime(dateTime, weeklyRepeater))
            } else {
                this
            }
        }

    private fun scheduleAlarm(context: Context, alarm: Alarm) {
        AlarmScheduler.scheduleAlarm(context, alarm.toAlarmExecutionData())
    }

    /*
     * Modify
     */

    fun updateName(name: String) {
        if (_newAlarm.value is AlarmState.Success) {
            val updatedAlarm = (_newAlarm.value as AlarmState.Success).alarm.copy(name = name)
            _newAlarm.value = AlarmState.Success(updatedAlarm)

            // Update validation state for TextField
            validateName(updatedAlarm)
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

    fun updateRingtone(ringtoneUri: String?) {
        if (
            _newAlarm.value is AlarmState.Success &&
            ringtoneUri != null &&
            ringtoneUri != RingtoneData.NO_RINGTONE_URI
        ) {
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            _newAlarm.value = AlarmState.Success(alarm.copy(ringtoneUri = ringtoneUri))
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
     * Snackbar
     */

    private fun sendSnackbarToPreviousScreen(navHostController: NavHostController, snackbarEvent: SnackbarEvent) {
        navHostController.previousBackStackEntry?.savedStateHandle
            ?.set(SnackbarEvent.KEY_SNACKBAR_EVENT_MESSAGE, snackbarEvent.message)
    }

    private suspend fun pushTriagedErrorToSnackbar() {
        val snackbarError: ValidationResult.Error<ValidationError>? =
            isDateTimeValid as? ValidationResult.Error
                ?: _isNameContentValid.value as? ValidationResult.Error
                ?: _isNameLengthValid.value as? ValidationResult.Error

        if (snackbarError != null) {
            try {
                snackbarChannel.send(snackbarError)
            } catch (e: Exception) {
                // SendChannel.send() can theoretically throw any type of Exception
                // if called on a Channel that is already closed.
                // Re-throw CancellationException.
                // No functionality beyond this is desired, so all other Exceptions
                // are simply consumed to prevent crashes.
                if (e is CancellationException) {
                    throw e
                }
            }
        }
    }

    /*
     * Navigation
     */

    fun tryNavigateUp(navHostController: NavHostController) {
        if (
            referenceAlarm.value is AlarmState.Success &&
            _newAlarm.value is AlarmState.Success
        ) {
            if (hasUnsavedChanges()) {
                _showUnsavedChangesDialog.value = true
            } else {
                navHostController.navigateUp()
            }
        }
    }

    fun tryNavigateBack(navHostController: NavHostController) {
        if (
            referenceAlarm.value is AlarmState.Success &&
            _newAlarm.value is AlarmState.Success
        ) {
            if (hasUnsavedChanges()) {
                _showUnsavedChangesDialog.value = true
            } else {
                navHostController.popBackStack()
            }
        }
    }

    /*
     * Dialog
     */

    fun unsavedChangesLeave(navHostController: NavHostController) {
        _showUnsavedChangesDialog.value = false
        // Doesn't code for navHostController.navigateUp(), but there's no
        // third party deeplinking into this app so it's fine.
        navHostController.popBackStack()
    }

    fun unsavedChangesStay() {
        _showUnsavedChangesDialog.value = false
    }

    /*
     * Validation
     */

    private fun validateAlarm(alarm: Alarm): Boolean {
        // Validate
        validateName(alarm)
        validateDateTime(alarm)

        // Check validation results
        return !(_isNameLengthValid.value is ValidationResult.Error ||
                _isNameContentValid.value is ValidationResult.Error ||
                isDateTimeValid is ValidationResult.Error)
    }

    private fun validateName(alarm: Alarm) {
        _isNameLengthValid.value = alarmValidator.validateNameLength(alarm.name)
        _isNameContentValid.value = alarmValidator.validateNameContent(alarm.name)
    }

    private fun validateDateTime(alarm: Alarm) {
        isDateTimeValid = alarmValidator.validateDateTime(alarm.dateTime)
    }

    private fun hasUnsavedChanges(): Boolean =
        if (
            referenceAlarm.value is AlarmState.Success &&
            _newAlarm.value is AlarmState.Success
        ) {
            val refAlarm = (referenceAlarm.value as AlarmState.Success).alarm
            val alarm = (_newAlarm.value as AlarmState.Success).alarm
            refAlarm != alarm
        } else {
            false
        }
}

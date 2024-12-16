package com.example.alarmscratch.alarm.ui.alarmcreateedit

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.preview.calendarAlarm
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.repeatingAlarm
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.AlarmDays
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.DateSelectionDialog
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.TimeSelectionDialog
import com.example.alarmscratch.alarm.validation.AlarmValidator
import com.example.alarmscratch.alarm.validation.ValidationError
import com.example.alarmscratch.alarm.validation.ValidationResult
import com.example.alarmscratch.core.extension.get12HourTime
import com.example.alarmscratch.core.extension.get24HourTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.snackbar.GlobalSnackbarController
import com.example.alarmscratch.core.ui.snackbar.SnackbarEvent
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.ui.alarmdefaults.component.SnoozeDurationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun AlarmCreateEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    @StringRes titleRes: Int,
    alarm: Alarm,
    alarmRingtoneName: String,
    timeDisplay: TimeDisplay,
    saveAndScheduleAlarm: (Context, suspend () -> Unit) -> Unit,
    updateName: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    toggleVibration: () -> Unit,
    updateSnoozeDuration: (Int) -> Unit,
    isNameValid: ValidationResult<AlarmValidator.NameError>,
    snackbarChannelFlow: Flow<ValidationResult.Error<ValidationError>>,
    modifier: Modifier = Modifier
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar
    LaunchedEffect(key1 = context, key2 = lifecycleOwner.lifecycle, key3 = snackbarChannelFlow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                snackbarChannelFlow.collect {
                    snackbarHostState.showSnackbar(message = it.error.toSnackbarString(context))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleRes = titleRes,
                navigationButton = {
                    IconButton(onClick = navHostController::navigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(
                        onClick = {
                            saveAndScheduleAlarm(context) {
                                // TODO: use real Alarm data
                                navHostController.popBackStack()
                                GlobalSnackbarController.sendEvent(SnackbarEvent("Alarm saved"))
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                },
                modifier = Modifier.background(color = MediumVolcanicRock)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = VolcanicRock,
                    contentColor = BoatSails
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Alarm Name, Date/Time Settings, Alert Settings, Snooze Settings
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                // Alarm Name
                OutlinedTextField(
                    value = alarm.name,
                    onValueChange = { updateName(it) },
                    placeholder = { Text(text = stringResource(id = R.string.alarm_name_placeholder), color = LightVolcanicRock) },
                    trailingIcon = if (isNameValid is ValidationResult.Error) {
                        { Icon(imageVector = Icons.Default.Error, contentDescription = null) }
                    } else {
                        null
                    },
                    supportingText = {
                        Text(
                            text = if (isNameValid is ValidationResult.Error) {
                                isNameValid.error.toInlineString(context)
                            } else {
                                "" // Empty String prevents Error text from shifting UI
                            }
                        )
                    },
                    isError = isNameValid is ValidationResult.Error,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkerBoatSails),
                    modifier = Modifier.padding(0.dp)
                )

                // Date/Time Settings
                DateTimeSettings(
                    alarm = alarm,
                    timeDisplay = timeDisplay,
                    updateDate = updateDate,
                    updateTime = updateTime,
                    addDay = addDay,
                    removeDay = removeDay
                )
                HorizontalDivider(
                    color = VolcanicRock,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }

            // Alert Settings
            AlertSettings(
                navigateToRingtonePickerScreen = { navigateToRingtonePickerScreen(alarm.ringtoneUriString) },
                selectedRingtone = alarmRingtoneName,
                isVibrationEnabled = alarm.isVibrationEnabled,
                toggleVibration = toggleVibration,
                modifier = Modifier.fillMaxWidth()
            )

            // Snooze Settings
            SnoozeSettings(
                snoozeDuration = alarm.snoozeDuration,
                updateSnoozeDuration = updateSnoozeDuration,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}

@Composable
fun DateTimeSettings(
    alarm: Alarm,
    timeDisplay: TimeDisplay,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit
) {
    // State
    var showDateSelectionDialog by rememberSaveable { mutableStateOf(false) }
    val toggleDateSelectionDialog: () -> Unit = { showDateSelectionDialog = !showDateSelectionDialog }

    Column {
        // Time
        AlarmTime(
            dateTime = alarm.dateTime,
            timeDisplay = timeDisplay,
            updateTime = updateTime,
            modifier = Modifier.padding(0.dp)
        )

        // Calendar Button and Alarm execution config
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Calendar Button
            IconButton(onClick = toggleDateSelectionDialog) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
            }

            // Alarm execution config
            AlarmDays(alarm = alarm, modifier = Modifier.padding(start = 4.dp))
        }

        // Day of Week
        DayOfWeekPicker(
            weeklyRepeater = alarm.weeklyRepeater,
            addDay = addDay,
            removeDay = removeDay
        )
    }

    // Date Selection Dialog
    if (showDateSelectionDialog) {
        DateSelectionDialog(
            alarmDateTime = alarm.dateTime,
            onCancel = toggleDateSelectionDialog,
            onConfirm = { date ->
                updateDate(date)
                toggleDateSelectionDialog()
            }
        )
    }
}

@Composable
fun AlarmTime(
    dateTime: LocalDateTime,
    timeDisplay: TimeDisplay,
    updateTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimePickerDialog: () -> Unit = { showTimePickerDialog = !showTimePickerDialog }
    val time = when (timeDisplay) {
        TimeDisplay.TwelveHour ->
            dateTime.get12HourTime()
        TimeDisplay.TwentyFourHour ->
            dateTime.get24HourTime()
    }

    // Time and AM/PM
    Row(
        modifier = modifier
            .background(color = DarkVolcanicRock)
            .clickable(onClick = toggleTimePickerDialog)
    ) {
        // Time
        Text(
            text = time,
            color = DarkerBoatSails,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alignByBaseline()
        )

        // AM/PM
        if (timeDisplay == TimeDisplay.TwelveHour) {
            Text(
                text = dateTime.getAmPm(LocalContext.current),
                color = DarkerBoatSails,
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alignByBaseline()
            )
        }
    }

    // Time Selection Dialog
    if (showTimePickerDialog) {
        TimeSelectionDialog(
            initialHour = dateTime.hour,
            initialMinute = dateTime.minute,
            timeDisplay = timeDisplay,
            onCancel = toggleTimePickerDialog,
            onConfirm = { hour, minute ->
                updateTime(hour, minute)
                toggleTimePickerDialog()
            }
        )
    }
}

@Composable
fun DayOfWeekPicker(
    weeklyRepeater: WeeklyRepeater,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        WeeklyRepeater.Day.entries.forEach { day ->
            DayOfWeekButton(
                dayText = day.oneLetterShorthand,
                selected = weeklyRepeater.isRepeatingOn(day),
                addDay = { addDay(day) },
                removeDay = { removeDay(day) }
            )
        }
    }
}

@Composable
fun DayOfWeekButton(
    dayText: String,
    selected: Boolean,
    addDay: () -> Unit,
    removeDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    val textColor = if (selected) BoatSails else LightVolcanicRock
    val borderColor = if (selected) BoatSails else LightVolcanicRock
    val border = BorderStroke(width = 1.dp, color = borderColor)

    OutlinedButton(
        onClick = { if (selected) removeDay() else addDay() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
        border = border,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
    ) {
        Text(text = dayText)
    }
}

@Composable
fun AlertSettings(
    navigateToRingtonePickerScreen: () -> Unit,
    selectedRingtone: String,
    isVibrationEnabled: Boolean,
    toggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Alert Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_alert),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Sound/Ringtone selection
        RowSelectionItem(
            rowOnClick = navigateToRingtonePickerScreen,
            rowLabelResId = R.string.alarm_create_edit_alarm_sound_label,
            choiceComponent = { Text(text = selectedRingtone) }
        )

        // Vibration toggle
        RowSelectionItem(
            rowOnClick = toggleVibration,
            rowLabelResId = R.string.alarm_create_edit_alarm_vibration_label,
            choiceComponent = {
                Switch(
                    checked = isVibrationEnabled,
                    onCheckedChange = { toggleVibration() },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = WayDarkerBoatSails,
                        uncheckedTrackColor = DarkVolcanicRock
                    )
                )
            }
        )
    }
}

@Composable
fun SnoozeSettings(
    snoozeDuration: Int,
    updateSnoozeDuration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showSnoozeDurationDialog by rememberSaveable { mutableStateOf(false) }
    val toggleSnoozeDurationDialog: () -> Unit = { showSnoozeDurationDialog = !showSnoozeDurationDialog }

    Column(modifier = modifier) {
        // Snooze Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.Snooze,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_snooze),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Snooze Duration
        RowSelectionItem(
            rowOnClick = toggleSnoozeDurationDialog,
            rowLabelResId = R.string.alarm_create_edit_alarm_snooze_duration,
            choiceComponent = { Text(text = "$snoozeDuration ${stringResource(id = R.string.snooze_minutes)}") }
        )
    }

    // Snooze Duration Dialog
    if (showSnoozeDurationDialog) {
        SnoozeDurationDialog(
            initialSnoozeDuration = snoozeDuration,
            onCancel = toggleSnoozeDurationDialog,
            onConfirm = { newSnoozeDuration ->
                updateSnoozeDuration(newSnoozeDuration)
                toggleSnoozeDurationDialog()
            }
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreateEditScreen12HourPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarChannelFlow = snackbarChannel.receiveAsFlow()

    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            isNameValid = ValidationResult.Success(),
            snackbarChannelFlow = snackbarChannelFlow
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreen24HourPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarChannelFlow = snackbarChannel.receiveAsFlow()

    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = calendarAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwentyFourHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            isNameValid = ValidationResult.Success(),
            snackbarChannelFlow = snackbarChannelFlow
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorIllegalCharacterPreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarChannelFlow = snackbarChannel.receiveAsFlow()

    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = "Illegal.String"),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            isNameValid = ValidationResult.Error(AlarmValidator.NameError.ILLEGAL_CHARACTER),
            snackbarChannelFlow = snackbarChannelFlow
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreenErrorOnlyWhitespacePreview() {
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarChannelFlow = snackbarChannel.receiveAsFlow()

    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm.copy(name = " "),
            alarmRingtoneName = sampleRingtoneData.name,
            timeDisplay = TimeDisplay.TwelveHour,
            saveAndScheduleAlarm = { _, _ -> },
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            isNameValid = ValidationResult.Error(AlarmValidator.NameError.ONLY_WHITESPACE),
            snackbarChannelFlow = snackbarChannelFlow
        )
    }
}

@Preview
@Composable
private fun DateTimeSettingsPreview() {
    AlarmScratchTheme {
        DateTimeSettings(
            alarm = consistentFutureAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmTimePreview() {
    AlarmScratchTheme {
        AlarmTime(
            dateTime = consistentFutureAlarm.dateTime,
            timeDisplay = TimeDisplay.TwelveHour,
            updateTime = { _, _ -> }
        )
    }
}

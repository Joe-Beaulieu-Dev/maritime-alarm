package com.example.alarmscratch.alarm.ui.alarmcreateedit

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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.alarmscratch.core.extension.get12HourTime
import com.example.alarmscratch.core.extension.get24HourTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails
import com.example.alarmscratch.core.util.StatusBarUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun AlarmCreateEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    @StringRes titleRes: Int,
    alarm: Alarm,
    alarmRingtoneName: String,
    is24Hour: Boolean,
    validateAlarm: () -> Boolean,
    saveAndScheduleAlarm: () -> Unit,
    updateName: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    toggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val snackbarString = stringResource(id = R.string.validation_alarm_in_past)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { snackbarMessage ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = snackbarMessage)
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
                            if (validateAlarm()) {
                                saveAndScheduleAlarm()
                                navHostController.popBackStack()
                            } else {
                                showSnackbar(snackbarString)
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
            // Alarm Name and Date/Time Settings
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                // TODO: Add validation
                // Alarm Name
                OutlinedTextField(
                    value = alarm.name,
                    onValueChange = { updateName(it) },
                    placeholder = { Text(text = stringResource(id = R.string.alarm_name_placeholder), color = LightVolcanicRock) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkerBoatSails),
                    modifier = Modifier.padding(0.dp)
                )

                // Date/Time Settings
                DateTimeSettings(
                    alarm = alarm,
                    is24Hour = is24Hour,
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

            // Alarm Alert Settings
            AlarmAlertSettings(
                navigateToRingtonePickerScreen = { navigateToRingtonePickerScreen(alarm.ringtoneUriString) },
                selectedRingtone = alarmRingtoneName,
                isVibrationEnabled = alarm.isVibrationEnabled,
                toggleVibration = toggleVibration,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DateTimeSettings(
    alarm: Alarm,
    is24Hour: Boolean,
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
            is24Hour = is24Hour,
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
            alarmTime = alarm.dateTime.toLocalTime(),
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
    is24Hour: Boolean,
    updateTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimePickerDialog: () -> Unit = { showTimePickerDialog = !showTimePickerDialog }
    val time = if (is24Hour) dateTime.get24HourTime() else dateTime.get12HourTime()

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
        if (!is24Hour) {
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
            is24Hour = is24Hour,
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
    var enabled by rememberSaveable { mutableStateOf(selected) }
    val borderColor = if (enabled) BoatSails else LightVolcanicRock
    val border = BorderStroke(width = 1.dp, color = borderColor)
    val textColor = if (enabled) BoatSails else LightVolcanicRock

    OutlinedButton(
        onClick = {
            enabled = !enabled
            if (enabled) {
                addDay()
            } else {
                removeDay()
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
        border = border,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
    ) {
        Text(text = dayText)
    }
}

@Composable
fun AlarmAlertSettings(
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

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreateEditScreen12HourPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = repeatingAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            is24Hour = false,
            validateAlarm = { true },
            saveAndScheduleAlarm = {},
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreateEditScreen24HourPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = calendarAlarm,
            alarmRingtoneName = sampleRingtoneData.name,
            is24Hour = true,
            validateAlarm = { true },
            saveAndScheduleAlarm = {},
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {},
            toggleVibration = {}
        )
    }
}

@Preview
@Composable
private fun DateTimeSettingsPreview() {
    AlarmScratchTheme {
        DateTimeSettings(
            alarm = consistentFutureAlarm,
            is24Hour = false,
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
            is24Hour = false,
            updateTime = { _, _ -> }
        )
    }
}

package com.example.alarmscratch.alarm.ui.alarmcreateedit

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.alarm.data.preview.tueWedThu
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.AlarmDays
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.DateSelectionDialog
import com.example.alarmscratch.alarm.ui.alarmcreateedit.component.TimeSelectionDialog
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.get12HrTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun AlarmCreateEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: () -> Unit,
    @StringRes titleRes: Int,
    alarm: Alarm,
    alarmRingtoneName: String,
    validateAlarm: () -> Boolean,
    saveAlarm: () -> Unit,
    scheduleAlarm: (Context) -> Unit,
    updateName: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { snackbarMessage ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = snackbarMessage)
        }
    }

    Scaffold(
        topBar = {
            AlarmCreationTopAppBar(
                navHostController = navHostController,
                titleRes = titleRes,
                showSnackbar = showSnackbar,
                validateAlarm = validateAlarm,
                saveAlarm = saveAlarm,
                scheduleAlarm = scheduleAlarm
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
        contentColor = BoatSails,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            // Alarm Name, and Date/Time Settings
            Column(
                modifier = Modifier
                    .padding(20.dp)
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
                    updateDate = updateDate,
                    updateTime = updateTime,
                    addDay = addDay,
                    removeDay = removeDay
                )
                HorizontalDivider(
                    color = VolcanicRock,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )

                // Alarm Alert Settings
                AlarmAlertSettings(
                    navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
                    selectedRingtone = alarmRingtoneName
                )
            }
        }
    }
}

// TODO: Use M3 TopAppBar once it's no longer experimental
@Composable
fun AlarmCreationTopAppBar(
    navHostController: NavHostController,
    @StringRes titleRes: Int,
    showSnackbar: (String) -> Unit,
    validateAlarm: () -> Boolean,
    saveAlarm: () -> Unit,
    scheduleAlarm: (Context) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = MediumVolcanicRock)
            .fillMaxWidth()
    ) {
        // Up Navigation Arrow and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Up Navigation Arrow
            IconButton(onClick = { navHostController.navigateUp() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }

            // Title
            Text(
                text = stringResource(id = titleRes),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        val context = LocalContext.current
        val snackbarString = stringResource(id = R.string.validation_alarm_in_past)
        // Save Button
        IconButton(
            onClick = {
                if (validateAlarm()) {
                    saveAlarm()
                    // TODO: Only schedule alarm if enabled
                    scheduleAlarm(context)
                    navHostController.popBackStack()
                } else {
                    showSnackbar(snackbarString)
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
        }
    }
}

@Composable
fun DateTimeSettings(
    alarm: Alarm,
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
    updateTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimePickerDialog: () -> Unit = { showTimePickerDialog = !showTimePickerDialog }

    // Time and AM/PM
    Row(
        modifier = modifier
            .background(color = DarkVolcanicRock)
            .clickable(onClick = toggleTimePickerDialog)
    ) {
        // Time
        Text(
            text = dateTime.get12HrTime(),
            color = DarkerBoatSails,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alignByBaseline()
        )

        // AM/PM
        Text(
            text = dateTime.getAmPm(LocalContext.current),
            color = DarkerBoatSails,
            fontSize = 38.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
    }

    // Time Selection Dialog
    if (showTimePickerDialog) {
        TimeSelectionDialog(
            initialHour = dateTime.hour,
            initialMinute = dateTime.minute,
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Alert Icon and Text
        Row {
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigateToRingtonePickerScreen() }
                .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
        ) {
            // Sound label
            Text(text = stringResource(id = R.string.alarm_create_edit_alarm_sound_label))
            // Ringtone name
            Text(text = selectedRingtone)
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreateEditScreenPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu),
                ringtoneUriString = sampleRingtoneData.baseUri
            ),
            alarmRingtoneName = sampleRingtoneData.name,
            validateAlarm = { true },
            saveAlarm = {},
            scheduleAlarm = {},
            updateName = {},
            updateDate = {},
            updateTime = { _, _ -> },
            addDay = {},
            removeDay = {}
        )
    }
}

@Preview
@Composable
private fun AlarmCreationTopAppBarPreview() {
    AlarmScratchTheme {
        AlarmCreationTopAppBar(
            navHostController = rememberNavController(),
            titleRes = R.string.alarm_creation_screen_title,
            showSnackbar = {},
            validateAlarm = { true },
            saveAlarm = {},
            scheduleAlarm = {}
        )
    }
}

@Preview
@Composable
private fun DateTimeSettingsPreview() {
    AlarmScratchTheme {
        DateTimeSettings(
            alarm = consistentFutureAlarm,
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
        AlarmTime(dateTime = consistentFutureAlarm.dateTime, updateTime = { _, _ -> })
    }
}

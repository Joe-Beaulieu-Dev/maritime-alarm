package com.example.alarmscratch.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.model.WeeklyRepeater
import com.example.alarmscratch.extension.LocalDateTimeUtil
import com.example.alarmscratch.extension.get12HrTime
import com.example.alarmscratch.ui.alarmcreation.AlarmDays
import com.example.alarmscratch.ui.alarmcreation.DateSelectionDialog
import com.example.alarmscratch.ui.alarmcreation.TimeSelectionDialog
import com.example.alarmscratch.ui.alarmlist.composable.amPm
import com.example.alarmscratch.ui.alarmlist.preview.consistentFutureAlarm
import com.example.alarmscratch.ui.alarmlist.preview.tueWedThu
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.BoatSails
import com.example.alarmscratch.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.ui.theme.DarkerBoatSails
import com.example.alarmscratch.ui.theme.LightVolcanicRock
import com.example.alarmscratch.ui.theme.MediumVolcanicRock
import java.time.LocalDate

@Composable
fun AlarmCreateEditScreen(
    navHostController: NavHostController,
    @StringRes titleRes: Int,
    alarm: Alarm,
    saveAlarm: () -> Unit,
    updateName: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // AppBar
            AlarmCreationTopAppBar(
                navHostController = navHostController,
                titleRes = titleRes,
                saveAlarm = saveAlarm
            )

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
            }
        }
    }
}

// TODO: Use M3 TopAppBar once it's no longer experimental
@Composable
fun AlarmCreationTopAppBar(
    navHostController: NavHostController,
    @StringRes titleRes: Int,
    saveAlarm: () -> Unit
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

        // Save Button
        IconButton(
            onClick = {
                saveAlarm()
                navHostController.popBackStack()
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
            alarm = alarm,
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
    alarm: Alarm,
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
            text = alarm.get12HrTime(),
            color = DarkerBoatSails,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alignByBaseline()
        )

        // AM/PM
        Text(
            text = amPm(alarm = alarm),
            color = DarkerBoatSails,
            fontSize = 38.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
    }

    // Time Selection Dialog
    if (showTimePickerDialog) {
        TimeSelectionDialog(
            initialHour = alarm.dateTime.hour,
            initialMinute = alarm.dateTime.minute,
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

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreateEditScreenPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu)
            ),
            saveAlarm = {},
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
            saveAlarm = {}
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
        AlarmTime(alarm = consistentFutureAlarm, updateTime = { _, _ -> })
    }
}

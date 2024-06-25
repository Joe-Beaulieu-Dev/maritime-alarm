package com.example.alarmscratch.ui.alarmcreation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.model.WeeklyRepeater
import com.example.alarmscratch.extension.get12HrTime
import com.example.alarmscratch.ui.alarmlist.composable.amPm
import com.example.alarmscratch.ui.alarmlist.preview.consistentFutureAlarm
import com.example.alarmscratch.ui.alarmlist.preview.tueWedThu
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.BoatSails
import com.example.alarmscratch.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.ui.theme.DarkerBoatSails
import com.example.alarmscratch.ui.theme.LightVolcanicRock
import com.example.alarmscratch.ui.theme.MediumVolcanicRock
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun AlarmCreationScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    alarmCreationViewModel: AlarmCreationViewModel = viewModel(factory = AlarmCreationViewModel.Factory)
) {
    // State
    val alarmState by alarmCreationViewModel.newAlarm.collectAsState()

    // Actions
    val coroutineScope = rememberCoroutineScope()
    val saveAlarm: () -> Unit = { coroutineScope.launch { alarmCreationViewModel.saveAlarm() } }
    val updateName: (String) -> Unit = alarmCreationViewModel::updateName
    val updateTime: (Int, Int) -> Unit = alarmCreationViewModel::updateTime
    val addDay: (WeeklyRepeater.Day) -> Unit = alarmCreationViewModel::addDay
    val removeDay: (WeeklyRepeater.Day) -> Unit = alarmCreationViewModel::removeDay

    AlarmCreationScreenContent(
        navHostController = navHostController,
        alarm = alarmState,
        saveAlarm = saveAlarm,
        updateName = updateName,
        updateTime = updateTime,
        addDay = addDay,
        removeDay = removeDay,
        modifier = modifier
    )
}

@Composable
fun AlarmCreationScreenContent(
    navHostController: NavHostController,
    alarm: Alarm,
    saveAlarm: () -> Unit,
    updateName: (String) -> Unit,
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AlarmCreationTopAppBar(
                navHostController = navHostController,
                saveAlarm = saveAlarm
            )
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // TODO: Add validation
                OutlinedTextField(
                    value = alarm.name,
                    onValueChange = { updateName(it) },
                    placeholder = { Text(text = stringResource(id = R.string.alarm_name_placeholder), color = LightVolcanicRock) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DarkerBoatSails),
                    modifier = Modifier.padding(0.dp)
                )
                DateTimeSettings(
                    alarm = alarm,
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
    saveAlarm: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = MediumVolcanicRock)
            .fillMaxWidth()
    ) {
        IconButton(onClick = { navHostController.navigateUp() }) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
        }
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
    updateTime: (Int, Int) -> Unit,
    addDay: (WeeklyRepeater.Day) -> Unit,
    removeDay: (WeeklyRepeater.Day) -> Unit
) {
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
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
            }

            // Alarm execution config
            Text(
                text = "Today",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Day of Week
        DayOfWeekPicker(
            weeklyRepeater = alarm.weeklyRepeater,
            addDay = addDay,
            removeDay = removeDay
        )
    }
}

@Composable
fun AlarmTime(
    alarm: Alarm,
    updateTime: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimePickerDialog: () -> Unit = { showTimePickerDialog = !showTimePickerDialog }

    Row(
        modifier = modifier
            .background(color = DarkVolcanicRock)
            .clickable(onClick = toggleTimePickerDialog)
    ) {
        Text(
            text = alarm.get12HrTime(),
            color = DarkerBoatSails,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = amPm(alarm = alarm),
            color = DarkerBoatSails,
            fontSize = 38.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
    }

    if (showTimePickerDialog) {
        AlarmTimePickerDialog(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onCancel: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)

    Dialog(onDismissRequest = onCancel) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp)) {
                // Title
                Text(text = stringResource(id = R.string.select_time), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(15.dp))

                // Time selection
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        selectorColor = LightVolcanicRock,
                        periodSelectorSelectedContainerColor = LightVolcanicRock,
                        periodSelectorSelectedContentColor = BoatSails,
                        periodSelectorUnselectedContentColor = LightVolcanicRock,
                        timeSelectorSelectedContainerColor = LightVolcanicRock
                    )
                )
            }

            // Bottom Button Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            ) {
                // Entry method toggle Button
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = DarkerBoatSails
                    )
                }

                // Cancel/Confirm Button Row
                Row {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(contentColor = LightVolcanicRock)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onConfirm(timePickerState.hour, timePickerState.minute) },
                        colors = ButtonDefaults.textButtonColors(contentColor = BoatSails)
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
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
                dayText = day.shorthand,
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

@Preview
@Composable
private fun AlarmCreationScreenPreview() {
    AlarmScratchTheme {
        AlarmCreationScreenContent(
            navHostController = rememberNavController(),
            alarm = Alarm(
                dateTime = LocalDateTime.now().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu)
            ),
            saveAlarm = {},
            updateName = {},
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

@Preview
@Composable
private fun AlarmTimePickerDialogPreview() {
    AlarmScratchTheme {
        AlarmTimePickerDialog(
            initialHour = 15,
            initialMinute = 45,
            onCancel = {},
            onConfirm = { _, _ -> }
        )
    }
}

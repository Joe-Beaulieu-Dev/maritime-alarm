package com.example.alarmscratch.alarm.ui.alarmlist.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.preview.calendarAlarm
import com.example.alarmscratch.alarm.data.preview.repeatingAlarm
import com.example.alarmscratch.alarm.data.preview.todayAlarm
import com.example.alarmscratch.alarm.data.preview.tomorrowAlarm
import com.example.alarmscratch.core.extension.get12HourTime
import com.example.alarmscratch.core.extension.get24HourTime
import com.example.alarmscratch.core.extension.getAmPm
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.settings.data.model.TimeDisplay

@Composable
fun AlarmCard(
    alarm: Alarm,
    timeDisplay: TimeDisplay,
    onAlarmToggled: (Context, Alarm) -> Unit,
    onAlarmDeleted: (Alarm) -> Unit,
    navigateToAlarmEditScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    val time = when (timeDisplay) {
        TimeDisplay.TwelveHour ->
            alarm.dateTime.get12HourTime()
        TimeDisplay.TwentyFourHour ->
            alarm.dateTime.get24HourTime()
    }

    // Colors
    val cardTextAndIconColor = if (alarm.enabled) BoatSails else MaterialTheme.colorScheme.outline
    val timeAmPmColor = if (alarm.enabled) DarkerBoatSails else MaterialTheme.colorScheme.outline
    val cardColor = if (alarm.enabled) MaterialTheme.colorScheme.surfaceVariant else MediumVolcanicRock

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = cardTextAndIconColor
        )
    ) {
        // Alarm content wrapper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigateToAlarmEditScreen(alarm.id) }
                .padding(start = 12.dp)
        ) {
            // Dropdown Menu
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                // Dropdown Icon Button
                IconButton(onClick = { dropdownExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = null
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(DarkVolcanicRock)
                ) {
                    // Delete
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.menu_delete))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = BoatHull
                            )
                        },
                        colors = MenuDefaults.itemColors(textColor = BoatSails),
                        onClick = {
                            onAlarmDeleted(alarm)
                            dropdownExpanded = false
                        }
                    )
                }
            }

            // Name, Time, and Date
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = if (alarm.name == "") 0.dp else 12.dp,
                        bottom = 12.dp
                    )
            ) {
                // Name
                Text(
                    text = alarm.name,
                    fontWeight = if (alarm.enabled) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
                )

                // Time and Date
                Column {
                    // Time
                    Row {
                        // Hour and Minute
                        Text(
                            text = time,
                            fontSize = 32.sp,
                            fontWeight = if (alarm.enabled) {
                                FontWeight.Bold
                            } else {
                                FontWeight.SemiBold
                            },
                            color = timeAmPmColor,
                            modifier = Modifier.alignByBaseline()
                        )

                        // AM/PM
                        if (timeDisplay == TimeDisplay.TwelveHour) {
                            Text(
                                text = alarm.dateTime.getAmPm(LocalContext.current),
                                fontWeight = if (alarm.enabled) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Medium
                                },
                                color = timeAmPmColor,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }

                    // Date
                    AlarmDate(
                        alarm = alarm,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            val context = LocalContext.current

            // Alarm Switch
            Switch(
                checked = alarm.enabled,
                onCheckedChange = { onAlarmToggled(context, alarm) },
                colors = SwitchDefaults.colors(uncheckedTrackColor = MediumVolcanicRock),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp)
            )
        }
    }
}

@Composable
fun NoAlarmsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = BoatSails
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 32.dp)
        ) {
            Text(
                text = stringResource(id = R.string.no_alarms),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmCardRepeating12HourPreview() {
    AlarmScratchTheme {
        AlarmCard(
            alarm = repeatingAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmCardRepeating24HourPreview() {
    AlarmScratchTheme {
        AlarmCard(
            alarm = todayAlarm,
            timeDisplay = TimeDisplay.TwentyFourHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun NoAlarmsCardPreview() {
    AlarmScratchTheme {
        NoAlarmsCard(
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmCardTodayPreview() {
    AlarmScratchTheme {
        AlarmCard(
            alarm = todayAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmCardTomorrowPreview() {
    AlarmScratchTheme {
        AlarmCard(
            alarm = tomorrowAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmCardCalendarPreview() {
    AlarmScratchTheme {
        AlarmCard(
            alarm = calendarAlarm,
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

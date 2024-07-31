package com.example.alarmscratch.alarm.ui.alarmedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.preview.tueWedThu
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmEditScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    alarmEditViewModel: AlarmEditViewModel = viewModel(factory = AlarmEditViewModel.Factory)
) {
    // State
    val alarmState by alarmEditViewModel.modifiedAlarm.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (alarmState is AlarmState.Success) {
        AlarmCreateEditScreen(
            navHostController = navHostController,
            titleRes = R.string.alarm_edit_screen_title,
            alarm = (alarmState as AlarmState.Success).alarm,
            validateAlarm = alarmEditViewModel::validateAlarm,
            saveAlarm = { coroutineScope.launch { alarmEditViewModel.saveAlarm() } },
            scheduleAlarm = alarmEditViewModel::scheduleAlarm,
            updateName = alarmEditViewModel::updateName,
            updateDate = alarmEditViewModel::updateDate,
            updateTime = alarmEditViewModel::updateTime,
            addDay = alarmEditViewModel::addDay,
            removeDay = alarmEditViewModel::removeDay,
            modifier = modifier
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmEditScreenPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            titleRes = R.string.alarm_edit_screen_title,
            alarm = Alarm(
                name = "Meeting",
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu)
            ),
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

package com.example.alarmscratch.alarm.ui.alarmcreate

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
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmCreationScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    alarmCreationViewModel: AlarmCreationViewModel = viewModel(factory = AlarmCreationViewModel.Factory)
) {
    // State
    val alarmState by alarmCreationViewModel.newAlarm.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    AlarmCreateEditScreen(
        navHostController = navHostController,
        titleRes = R.string.alarm_creation_screen_title,
        alarm = alarmState,
        saveAlarm = { coroutineScope.launch { alarmCreationViewModel.saveAlarm() } },
        scheduleAlarm = alarmCreationViewModel::scheduleAlarm,
        updateName = alarmCreationViewModel::updateName,
        updateDate = alarmCreationViewModel::updateDate,
        updateTime = alarmCreationViewModel::updateTime,
        addDay = alarmCreationViewModel::addDay,
        removeDay = alarmCreationViewModel::removeDay,
        modifier = modifier
    )
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmCreationScreenPreview() {
    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu)
            ),
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

package com.example.alarmscratch.alarm.ui.alarmcreate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.model.WeeklyRepeater
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.alarm.data.preview.tueWedThu
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.getRingtone
import com.example.alarmscratch.core.extension.getStringFromBackStack
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmCreationScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    alarmCreationViewModel: AlarmCreationViewModel = viewModel(factory = AlarmCreationViewModel.Factory)
) {
    // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
    // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
    // then the new Ringtone's URI will be saved here.
    alarmCreationViewModel.updateRingtone(
        navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI_STRING)
    )

    // State
    val alarmState by alarmCreationViewModel.newAlarm.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // This was extracted for previews, since previews can't actually "get a Ringtone"
    // from anywhere, therefore they can't get a name to display in the preview.
    val alarmRingtoneName = alarmState.getRingtone(context).getTitle(context)

    AlarmCreateEditScreen(
        navHostController = navHostController,
        navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
        titleRes = R.string.alarm_creation_screen_title,
        alarm = alarmState,
        alarmRingtoneName = alarmRingtoneName,
        validateAlarm = alarmCreationViewModel::validateAlarm,
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
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu),
                ringtoneUriString = sampleRingtoneData.fullUriString
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

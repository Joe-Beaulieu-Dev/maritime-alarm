package com.example.alarmscratch.alarm.ui.alarmcreate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.getRingtone
import com.example.alarmscratch.core.extension.getStringFromBackStack
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState

@Composable
fun AlarmCreationScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    alarmCreationViewModel: AlarmCreationViewModel = viewModel(factory = AlarmCreationViewModel.Factory)
) {
    // State
    val alarmState by alarmCreationViewModel.newAlarm.collectAsState()
    val generalSettingsState by alarmCreationViewModel.generalSettings.collectAsState()

    if (alarmState is AlarmState.Success && generalSettingsState is GeneralSettingsState.Success) {
        // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
        // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
        // then the new Ringtone's URI will be saved here.
        alarmCreationViewModel.updateRingtone(
            navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI_STRING)
        )

        val context = LocalContext.current
        val alarm = (alarmState as AlarmState.Success).alarm
        // This was extracted for previews, since previews can't actually "get a Ringtone"
        // from anywhere, therefore they can't get a name to display in the preview.
        val alarmRingtoneName = alarm.getRingtone(context).getTitle(context)
        val generalSettings = (generalSettingsState as GeneralSettingsState.Success).generalSettings
        val is24Hour =
            when (generalSettings.timeDisplay) {
                TimeDisplay.TwelveHour ->
                    false
                TimeDisplay.TwentyFourHour ->
                    true
            }

        AlarmCreateEditScreen(
            navHostController = navHostController,
            navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
            titleRes = R.string.alarm_creation_screen_title,
            alarm = alarm,
            alarmRingtoneName = alarmRingtoneName,
            is24Hour = is24Hour,
            validateAlarm = alarmCreationViewModel::validateAlarm,
            saveAndScheduleAlarm = { alarmCreationViewModel.saveAndScheduleAlarm(context) },
            updateName = alarmCreationViewModel::updateName,
            updateDate = alarmCreationViewModel::updateDate,
            updateTime = alarmCreationViewModel::updateTime,
            addDay = alarmCreationViewModel::addDay,
            removeDay = alarmCreationViewModel::removeDay,
            toggleVibration = alarmCreationViewModel::toggleVibration,
            modifier = modifier
        )
    }
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
                ringtoneUriString = sampleRingtoneData.fullUriString,
                isVibrationEnabled = true
            ),
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

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
import com.example.alarmscratch.alarm.validation.ValidationError
import com.example.alarmscratch.alarm.validation.ValidationResult
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.getRingtone
import com.example.alarmscratch.core.extension.getStringFromBackStack
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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
    val isNameValid by alarmCreationViewModel.isNameValid.collectAsState()

    // Flow
    val snackbarFlow = alarmCreationViewModel.snackbarFlow

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

        AlarmCreateEditScreen(
            navHostController = navHostController,
            navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
            titleRes = R.string.alarm_creation_screen_title,
            alarm = alarm,
            alarmRingtoneName = alarmRingtoneName,
            timeDisplay = generalSettings.timeDisplay,
            saveAndScheduleAlarm = alarmCreationViewModel::saveAndScheduleAlarm,
            updateName = alarmCreationViewModel::updateName,
            updateDate = alarmCreationViewModel::updateDateAndResetWeeklyRepeater,
            updateTime = alarmCreationViewModel::updateTime,
            addDay = alarmCreationViewModel::addDay,
            removeDay = alarmCreationViewModel::removeDay,
            toggleVibration = alarmCreationViewModel::toggleVibration,
            updateSnoozeDuration = alarmCreationViewModel::updateSnoozeDuration,
            isNameValid = isNameValid,
            snackbarFlow = snackbarFlow,
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
    val snackbarChannel = Channel<ValidationResult.Error<ValidationError>>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    AlarmScratchTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu),
                ringtoneUriString = sampleRingtoneData.fullUriString,
                isVibrationEnabled = true,
                snoozeDuration = 10
            ),
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
            snackbarFlow = snackbarFlow
        )
    }
}

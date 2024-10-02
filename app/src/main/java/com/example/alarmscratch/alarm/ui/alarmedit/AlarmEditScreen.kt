package com.example.alarmscratch.alarm.ui.alarmedit

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
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.extension.getRingtone
import com.example.alarmscratch.core.extension.getStringFromBackStack
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    alarmEditViewModel: AlarmEditViewModel = viewModel(factory = AlarmEditViewModel.Factory)
) {
    // State
    val alarmState by alarmEditViewModel.modifiedAlarm.collectAsState()

    if (alarmState is AlarmState.Success) {
        // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
        // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
        // then the new Ringtone's URI will be saved here.
        alarmEditViewModel.updateRingtone(
            navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI_STRING)
        )

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val alarm = (alarmState as AlarmState.Success).alarm
        // This was extracted for previews, since previews can't actually "get a Ringtone"
        // from anywhere, therefore they can't get a name to display in the preview.
        val alarmRingtoneName = alarm.getRingtone(context).getTitle(context)

        AlarmCreateEditScreen(
            navHostController = navHostController,
            navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
            titleRes = R.string.alarm_edit_screen_title,
            alarm = alarm,
            alarmRingtoneName = alarmRingtoneName,
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
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_edit_screen_title,
            alarm = Alarm(
                name = "Meeting",
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

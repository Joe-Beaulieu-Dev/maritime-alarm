package com.octrobi.lavalarm.alarm.ui.alarmedit

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.octrobi.lavalarm.R
import com.octrobi.lavalarm.alarm.data.model.Alarm
import com.octrobi.lavalarm.alarm.data.model.WeeklyRepeater
import com.octrobi.lavalarm.alarm.data.preview.sampleRingtoneData
import com.octrobi.lavalarm.alarm.data.preview.tueWedThu
import com.octrobi.lavalarm.alarm.data.repository.AlarmState
import com.octrobi.lavalarm.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.octrobi.lavalarm.alarm.validation.AlarmValidator
import com.octrobi.lavalarm.alarm.validation.ValidationError
import com.octrobi.lavalarm.alarm.validation.ValidationResult
import com.octrobi.lavalarm.core.data.model.RingtoneData
import com.octrobi.lavalarm.core.extension.LocalDateTimeUtil
import com.octrobi.lavalarm.core.extension.getRingtone
import com.octrobi.lavalarm.core.extension.getStringFromBackStack
import com.octrobi.lavalarm.core.ui.notificationcheck.NotificationChannelGateScreen
import com.octrobi.lavalarm.core.ui.notificationcheck.NotificationPermission
import com.octrobi.lavalarm.core.ui.permission.Permission
import com.octrobi.lavalarm.core.ui.permission.PermissionGateScreen
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.util.StatusBarUtil
import com.octrobi.lavalarm.settings.data.model.TimeDisplay
import com.octrobi.lavalarm.settings.data.repository.GeneralSettingsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun AlarmEditScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    alarmEditViewModel: AlarmEditViewModel = viewModel(factory = AlarmEditViewModel.Factory)
) {
    // State
    val alarmState by alarmEditViewModel.modifiedAlarm.collectAsState()
    val generalSettingsState by alarmEditViewModel.generalSettings.collectAsState()
    val isNameLengthValid by alarmEditViewModel.isNameLengthValid.collectAsState()
    val isNameContentValid by alarmEditViewModel.isNameContentValid.collectAsState()
    val showUnsavedChangesDialog by alarmEditViewModel.showUnsavedChangesDialog.collectAsState()

    // Flow
    val snackbarFlow = alarmEditViewModel.snackbarFlow

    val alarmCreateEditScreen: @Composable () -> Unit = {
        if (alarmState is AlarmState.Success && generalSettingsState is GeneralSettingsState.Success) {
            // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
            // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
            // then the new Ringtone's URI will be saved here.
            alarmEditViewModel.updateRingtone(
                navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI)
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
                titleRes = R.string.alarm_edit_screen_title,
                alarm = alarm,
                alarmRingtoneName = alarmRingtoneName,
                timeDisplay = generalSettings.timeDisplay,
                saveAndScheduleAlarm = alarmEditViewModel::saveAndScheduleAlarm,
                updateName = alarmEditViewModel::updateName,
                updateDate = alarmEditViewModel::updateDateAndResetWeeklyRepeater,
                updateTime = alarmEditViewModel::updateTime,
                addDay = alarmEditViewModel::addDay,
                removeDay = alarmEditViewModel::removeDay,
                toggleVibration = alarmEditViewModel::toggleVibration,
                updateSnoozeDuration = alarmEditViewModel::updateSnoozeDuration,
                nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
                isNameLengthValid = isNameLengthValid,
                isNameContentValid = isNameContentValid,
                snackbarFlow = snackbarFlow,
                tryNavigateUp = { alarmEditViewModel.tryNavigateUp(navHostController) },
                tryNavigateBack = { alarmEditViewModel.tryNavigateBack(navHostController) },
                showUnsavedChangesDialog = showUnsavedChangesDialog,
                unsavedChangesLeave = { alarmEditViewModel.unsavedChangesLeave(navHostController) },
                unsavedChangesStay = alarmEditViewModel::unsavedChangesStay,
                modifier = modifier
            )
        }
    }

    // Gate AlarmCreateEditScreen behind Alarm Notification Channel
    val notificationGatedAlarmEdit: @Composable () -> Unit = {
        NotificationChannelGateScreen(
            notificationPermission = NotificationPermission.Alarm,
            gatedScreen = alarmCreateEditScreen,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        )
    }

    // TODO: This permission gate is a stopgap. In the future I want to pop a Dialog when the save button
    //  is pressed if a permission is missing, rather than replacing the entire screen like below.
    // POST_NOTIFICATIONS permission was introduced in API 33 (TIRAMISU).
    // SCHEDULE_EXACT_ALARM permission is only required for APIs < 33 because
    // alarm apps can instead use USE_EXACT_ALARM on API 33+ which cannot be revoked.
    // Therefore, we only need to ask for one or the other, never both.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Configure Status Bar
        StatusBarUtil.setDarkStatusBar()

        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            PermissionGateScreen(
                permission = Permission.PostNotifications,
                gatedScreen = notificationGatedAlarmEdit,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            )
        }
    } else {
        // Configure Status Bar
        StatusBarUtil.setDarkStatusBar()

        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            PermissionGateScreen(
                permission = Permission.ScheduleExactAlarm,
                gatedScreen = notificationGatedAlarmEdit,
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
            )
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmEditScreenPreview() {
    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_edit_screen_title,
            alarm = Alarm(
                name = "Meeting",
                dateTime = LocalDateTimeUtil.nowTruncated().plusHours(1),
                weeklyRepeater = WeeklyRepeater(tueWedThu),
                ringtoneUri = sampleRingtoneData.fullUri,
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
            nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
            isNameLengthValid = ValidationResult.Success(),
            isNameContentValid = ValidationResult.Success(),
            snackbarFlow = Channel<ValidationResult.Error<ValidationError>>().receiveAsFlow(),
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

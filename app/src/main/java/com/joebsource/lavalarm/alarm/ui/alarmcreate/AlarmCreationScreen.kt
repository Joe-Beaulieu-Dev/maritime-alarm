package com.joebsource.lavalarm.alarm.ui.alarmcreate

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
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.model.Alarm
import com.joebsource.lavalarm.alarm.data.model.WeeklyRepeater
import com.joebsource.lavalarm.alarm.data.preview.sampleRingtoneData
import com.joebsource.lavalarm.alarm.data.preview.tueWedThu
import com.joebsource.lavalarm.alarm.data.repository.AlarmState
import com.joebsource.lavalarm.alarm.ui.alarmcreateedit.AlarmCreateEditScreen
import com.joebsource.lavalarm.alarm.validation.AlarmValidator
import com.joebsource.lavalarm.alarm.validation.ValidationError
import com.joebsource.lavalarm.alarm.validation.ValidationResult
import com.joebsource.lavalarm.core.data.model.RingtoneData
import com.joebsource.lavalarm.core.extension.LocalDateTimeUtil
import com.joebsource.lavalarm.core.extension.getRingtone
import com.joebsource.lavalarm.core.extension.getStringFromBackStack
import com.joebsource.lavalarm.core.ui.notificationcheck.NotificationChannelGateScreen
import com.joebsource.lavalarm.core.ui.notificationcheck.NotificationPermission
import com.joebsource.lavalarm.core.ui.permission.Permission
import com.joebsource.lavalarm.core.ui.permission.PermissionGateScreen
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.util.StatusBarUtil
import com.joebsource.lavalarm.settings.data.model.TimeDisplay
import com.joebsource.lavalarm.settings.data.repository.GeneralSettingsState
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
    val isNameLengthValid by alarmCreationViewModel.isNameLengthValid.collectAsState()
    val isNameContentValid by alarmCreationViewModel.isNameContentValid.collectAsState()
    val showUnsavedChangesDialog by alarmCreationViewModel.showUnsavedChangesDialog.collectAsState()

    // Flow
    val snackbarFlow = alarmCreationViewModel.snackbarFlow

    val alarmCreateEditScreen: @Composable () -> Unit = {
        if (alarmState is AlarmState.Success && generalSettingsState is GeneralSettingsState.Success) {
            // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
            // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
            // then the new Ringtone's URI will be saved here.
            alarmCreationViewModel.updateRingtone(
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
                nameCharacterLimit = AlarmValidator.NAME_CHARACTER_LIMIT,
                isNameLengthValid = isNameLengthValid,
                isNameContentValid = isNameContentValid,
                snackbarFlow = snackbarFlow,
                tryNavigateUp = { alarmCreationViewModel.tryNavigateUp(navHostController) },
                tryNavigateBack = { alarmCreationViewModel.tryNavigateBack(navHostController) },
                showUnsavedChangesDialog = showUnsavedChangesDialog,
                unsavedChangesLeave = { alarmCreationViewModel.unsavedChangesLeave(navHostController) },
                unsavedChangesStay = alarmCreationViewModel::unsavedChangesStay,
                modifier = modifier
            )
        }
    }

    // Gate AlarmCreateEditScreen behind Alarm Notification Channel
    val notificationGatedAlarmCreation: @Composable () -> Unit = {
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
                gatedScreen = notificationGatedAlarmCreation,
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
                gatedScreen = notificationGatedAlarmCreation,
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
private fun AlarmCreationScreenPreview() {
    LavalarmTheme {
        AlarmCreateEditScreen(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            titleRes = R.string.alarm_creation_screen_title,
            alarm = Alarm(
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

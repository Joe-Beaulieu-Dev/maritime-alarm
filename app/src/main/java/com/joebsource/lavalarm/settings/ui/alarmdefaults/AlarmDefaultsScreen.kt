package com.joebsource.lavalarm.settings.ui.alarmdefaults

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.preview.sampleRingtoneData
import com.joebsource.lavalarm.core.data.model.RingtoneData
import com.joebsource.lavalarm.core.extension.getStringFromBackStack
import com.joebsource.lavalarm.core.ui.shared.CustomTopAppBar
import com.joebsource.lavalarm.core.ui.shared.RowSelectionItem
import com.joebsource.lavalarm.core.ui.shared.UnsavedChangesDialog
import com.joebsource.lavalarm.core.ui.theme.BoatSails
import com.joebsource.lavalarm.core.ui.theme.DarkVolcanicRock
import com.joebsource.lavalarm.core.ui.theme.DarkerBoatSails
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.MediumVolcanicRock
import com.joebsource.lavalarm.core.ui.theme.VolcanicRock
import com.joebsource.lavalarm.core.ui.theme.WayDarkerBoatSails
import com.joebsource.lavalarm.core.util.StatusBarUtil
import com.joebsource.lavalarm.settings.data.repository.AlarmDefaultsState
import com.joebsource.lavalarm.settings.extension.getRingtone
import com.joebsource.lavalarm.settings.ui.alarmdefaults.component.SnoozeDurationDialog

@Composable
fun AlarmDefaultsScreen(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    alarmDefaultsViewModel: AlarmDefaultsViewModel = viewModel(factory = AlarmDefaultsViewModel.Factory)
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val alarmDefaultsState by alarmDefaultsViewModel.modifiedAlarmDefaults.collectAsState()
    val showUnsavedChangesDialog by alarmDefaultsViewModel.showUnsavedChangesDialog.collectAsState()

    if (alarmDefaultsState is AlarmDefaultsState.Success) {
        // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
        // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
        // then the new Ringtone's URI will be saved here.
        alarmDefaultsViewModel.updateRingtone(
            navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI)
        )

        val context = LocalContext.current
        val alarmDefaults = (alarmDefaultsState as AlarmDefaultsState.Success).alarmDefaults
        // This was extracted for previews, since previews can't actually "get a Ringtone"
        // from anywhere, therefore they can't get a name to display in the preview.
        val ringtoneName = alarmDefaults.getRingtone(context).getTitle(context)

        AlarmDefaultsScreenContent(
            navHostController = navHostController,
            navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
            ringtoneName = ringtoneName,
            ringtoneUri = alarmDefaults.ringtoneUri,
            isVibrationEnabled = alarmDefaults.isVibrationEnabled,
            snoozeDuration = alarmDefaults.snoozeDuration,
            saveAlarmDefaults = alarmDefaultsViewModel::saveAlarmDefaults,
            toggleVibration = alarmDefaultsViewModel::toggleVibration,
            updateSnoozeDuration = alarmDefaultsViewModel::updateSnoozeDuration,
            tryNavigateUp = { alarmDefaultsViewModel.tryNavigateUp(navHostController) },
            tryNavigateBack = { alarmDefaultsViewModel.tryNavigateBack(navHostController) },
            showUnsavedChangesDialog = showUnsavedChangesDialog,
            unsavedChangesLeave = { alarmDefaultsViewModel.unsavedChangesLeave(navHostController) },
            unsavedChangesStay = alarmDefaultsViewModel::unsavedChangesStay,
            modifier = modifier
        )
    }
}

@Composable
fun AlarmDefaultsScreenContent(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: (String) -> Unit,
    ringtoneName: String,
    ringtoneUri: String,
    isVibrationEnabled: Boolean,
    snoozeDuration: Int,
    saveAlarmDefaults: () -> Unit,
    toggleVibration: () -> Unit,
    updateSnoozeDuration: (Int) -> Unit,
    tryNavigateUp: () -> Unit,
    tryNavigateBack: () -> Unit,
    showUnsavedChangesDialog: Boolean,
    unsavedChangesLeave: () -> Unit,
    unsavedChangesStay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept system back navigation
    BackHandler {
        tryNavigateBack()
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleRes = R.string.settings_alarm_defaults,
                navigationButton = {
                    IconButton(onClick = tryNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(
                        onClick = {
                            saveAlarmDefaults()
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                },
                modifier = Modifier.background(color = MediumVolcanicRock)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Important Notice Header and Body
            Column(modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)) {
                // Important Notice Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notice Header Icon
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = DarkerBoatSails
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Notice Header Text
                    Text(
                        text = stringResource(id = R.string.alarm_defaults_important_notice_header),
                        color = DarkerBoatSails,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Important Notice Body
                Text(
                    text = stringResource(id = R.string.alarm_defaults_important_notice_body),
                    modifier = Modifier
                        .border(
                            border = BorderStroke(width = 1.dp, color = BoatSails),
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(8.dp)
                )
                HorizontalDivider(
                    color = VolcanicRock,
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                )
            }

            // Alert Defaults
            AlertDefaults(
                navigateToRingtonePickerScreen = { navigateToRingtonePickerScreen(ringtoneUri) },
                selectedRingtone = ringtoneName,
                isVibrationEnabled = isVibrationEnabled,
                toggleVibration = toggleVibration
            )

            // Snooze Defaults
            SnoozeDefaults(
                snoozeDuration = snoozeDuration,
                updateSnoozeDuration = updateSnoozeDuration,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }

    // Unsaved Changes Dialog
    if (showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onLeave = unsavedChangesLeave,
            onStay = unsavedChangesStay
        )
    }
}

@Composable
fun AlertDefaults(
    navigateToRingtonePickerScreen: () -> Unit,
    selectedRingtone: String,
    isVibrationEnabled: Boolean,
    toggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Alert Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_alert),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Sound/Ringtone selection
        RowSelectionItem(
            rowOnClick = navigateToRingtonePickerScreen,
            rowLabelResId = R.string.alarm_create_edit_alarm_sound_label,
            choiceComponent = { Text(text = selectedRingtone) }
        )

        // Vibration toggle
        RowSelectionItem(
            rowOnClick = toggleVibration,
            rowLabelResId = R.string.alarm_create_edit_alarm_vibration_label,
            choiceComponent = {
                Switch(
                    checked = isVibrationEnabled,
                    onCheckedChange = { toggleVibration() },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = WayDarkerBoatSails,
                        uncheckedTrackColor = DarkVolcanicRock
                    )
                )
            }
        )
    }
}

@Composable
fun SnoozeDefaults(
    snoozeDuration: Int,
    updateSnoozeDuration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showSnoozeDurationDialog by rememberSaveable { mutableStateOf(false) }
    val toggleSnoozeDurationDialog: () -> Unit = { showSnoozeDurationDialog = !showSnoozeDurationDialog }

    Column(modifier = modifier) {
        // Snooze Icon and Text
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Icon(
                imageVector = Icons.Default.Snooze,
                contentDescription = null,
                tint = DarkerBoatSails
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.section_snooze),
                color = DarkerBoatSails,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Snooze Duration
        RowSelectionItem(
            rowOnClick = toggleSnoozeDurationDialog,
            rowLabelResId = R.string.alarm_create_edit_alarm_snooze_duration,
            choiceComponent = { Text(text = "$snoozeDuration ${stringResource(id = R.string.snooze_minutes)}") }
        )
    }

    // Snooze Duration Dialog
    if (showSnoozeDurationDialog) {
        SnoozeDurationDialog(
            initialSnoozeDuration = snoozeDuration,
            onCancel = toggleSnoozeDurationDialog,
            onConfirm = { newSnoozeDuration ->
                updateSnoozeDuration(newSnoozeDuration)
                toggleSnoozeDurationDialog()
            }
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmDefaultsScreenPreview() {
    LavalarmTheme {
        AlarmDefaultsScreenContent(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            ringtoneName = sampleRingtoneData.name,
            ringtoneUri = sampleRingtoneData.fullUri,
            isVibrationEnabled = true,
            snoozeDuration = 10,
            saveAlarmDefaults = {},
            toggleVibration = {},
            updateSnoozeDuration = {},
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {}
        )
    }
}

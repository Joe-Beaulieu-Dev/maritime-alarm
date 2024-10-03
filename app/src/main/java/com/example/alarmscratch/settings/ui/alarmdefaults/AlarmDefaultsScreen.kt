package com.example.alarmscratch.settings.ui.alarmdefaults

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.extension.getStringFromBackStack
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.data.repository.AlarmDefaultsState
import com.example.alarmscratch.settings.extension.getRingtone
import kotlinx.coroutines.launch

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

    if (alarmDefaultsState is AlarmDefaultsState.Success) {
        // Fetch updated Ringtone URI from this back stack entry's SavedStateHandle.
        // If the User navigated to the RingtonePickerScreen and selected a new Ringtone,
        // then the new Ringtone's URI will be saved here.
        alarmDefaultsViewModel.updateRingtone(
            navHostController.getStringFromBackStack(RingtoneData.KEY_FULL_RINGTONE_URI_STRING)
        )

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val alarmDefaults = (alarmDefaultsState as AlarmDefaultsState.Success).alarmDefaults
        val ringtoneName = alarmDefaults.getRingtone(context).getTitle(context)

        AlarmDefaultsScreenContent(
            navHostController = navHostController,
            navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
            ringtoneName = ringtoneName,
            ringtoneUri = alarmDefaults.ringtoneUri,
            isVibrationEnabled = alarmDefaults.isVibrationEnabled,
            saveAlarmDefaults = { coroutineScope.launch { alarmDefaultsViewModel.saveAlarmDefaults() } },
            toggleVibration = alarmDefaultsViewModel::toggleVibration,
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
    saveAlarmDefaults: () -> Unit,
    toggleVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleRes = R.string.settings_alarm_defaults,
                navigationButton = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
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

            // Alarm Alert Defaults
            AlarmAlertDefaults(
                navigateToRingtonePickerScreen = { navigateToRingtonePickerScreen(ringtoneUri) },
                selectedRingtone = ringtoneName,
                isVibrationEnabled = isVibrationEnabled,
                toggleVibration = toggleVibration
            )
        }
    }
}

@Composable
fun AlarmAlertDefaults(
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

/*
 * Previews
 */

@Preview
@Composable
private fun AlarmDefaultsScreenPreview() {
    AlarmScratchTheme {
        AlarmDefaultsScreenContent(
            navHostController = rememberNavController(),
            navigateToRingtonePickerScreen = {},
            ringtoneName = sampleRingtoneData.name,
            ringtoneUri = sampleRingtoneData.fullUriString,
            isVibrationEnabled = true,
            saveAlarmDefaults = {},
            toggleVibration = {}
        )
    }
}

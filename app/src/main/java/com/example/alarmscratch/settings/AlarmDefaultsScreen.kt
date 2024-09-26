package com.example.alarmscratch.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.alarm.ui.alarmcreateedit.AlarmSettingsRowItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.ui.theme.WayDarkerBoatSails
import com.example.alarmscratch.core.util.StatusBarUtil

@Composable
fun AlarmDefaultsScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // TODO: Temporary code
    val alarmRingtoneName = "Temp Ringtone"
    val navigateToRingtonePickerScreen = {}

    AlarmDefaultsScreenContent(
        navHostController = navHostController,
        navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
        alarmRingtoneName = alarmRingtoneName,
        modifier = modifier
    )
}

@Composable
fun AlarmDefaultsScreenContent(
    navHostController: NavHostController,
    navigateToRingtonePickerScreen: () -> Unit,
    alarmRingtoneName: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AlarmDefaultsTopAppBar(
                navHostController = navHostController,
                titleRes = R.string.settings_alarm_defaults
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
                navigateToRingtonePickerScreen = navigateToRingtonePickerScreen,
                selectedRingtone = alarmRingtoneName
            )
        }
    }
}

@Composable
fun AlarmDefaultsTopAppBar(
    navHostController: NavHostController,
    @StringRes titleRes: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = MediumVolcanicRock)
            .fillMaxWidth()
    ) {
        // Up Navigation Arrow and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Up Navigation Arrow
            IconButton(onClick = { navHostController.navigateUp() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }

            // Title
            Text(
                text = stringResource(id = titleRes),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // Save Button
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
        }
    }
}

@Composable
fun AlarmAlertDefaults(
    navigateToRingtonePickerScreen: () -> Unit,
    selectedRingtone: String,
    modifier: Modifier = Modifier
) {
    // Temporary state
    var vibrationEnabled by rememberSaveable { mutableStateOf(false) }
    val toggleVibration: () -> Unit = { vibrationEnabled = !vibrationEnabled }

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
        AlarmSettingsRowItem(
            rowOnClick = navigateToRingtonePickerScreen,
            rowLabelResId = R.string.alarm_create_edit_alarm_sound_label,
            choiceComponent = { Text(text = selectedRingtone) }
        )

        // Vibration toggle
        AlarmSettingsRowItem(
            rowOnClick = toggleVibration,
            rowLabelResId = R.string.alarm_create_edit_alarm_vibration_label,
            choiceComponent = {
                Switch(
                    checked = vibrationEnabled,
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
            alarmRingtoneName = sampleRingtoneData.name
        )
    }
}

@Preview
@Composable
private fun AlarmDefaultsTopAppBarPreview() {
    AlarmScratchTheme {
        AlarmDefaultsTopAppBar(
            navHostController = rememberNavController(),
            titleRes = R.string.settings_alarm_defaults
        )
    }
}

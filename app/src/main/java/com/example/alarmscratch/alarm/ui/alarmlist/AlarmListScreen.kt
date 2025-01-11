package com.example.alarmscratch.alarm.ui.alarmlist

import android.Manifest
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.component.AlarmCard
import com.example.alarmscratch.alarm.ui.alarmlist.component.NoAlarmsCard
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState

@Composable
fun AlarmListScreen(
    navigateToAlarmEditScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    alarmListViewModel: AlarmListViewModel = viewModel(factory = AlarmListViewModel.Factory)
) {
    // State
    val alarmListState by alarmListViewModel.alarmList.collectAsState()
    val generalSettingsState by alarmListViewModel.generalSettings.collectAsState()

    // Permissions
    val localContext = LocalContext.current
    val hasNotificationPermission by alarmListViewModel.hasNotificationPermission.collectAsState()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        alarmListViewModel.checkNotificationPermission(localContext)
    }

    // Check for Notification Permission
    alarmListViewModel.checkNotificationPermission(localContext)

    if (!hasNotificationPermission) {
        NotificationPermissionOff(promptPermission = notificationPermissionLauncher)
    } else {
        if (alarmListState is AlarmListState.Success && generalSettingsState is GeneralSettingsState.Success) {
            val alarmList = (alarmListState as AlarmListState.Success).alarmList
            val generalSettings = (generalSettingsState as GeneralSettingsState.Success).generalSettings

            AlarmListScreenContent(
                alarmList = alarmList,
                timeDisplay = generalSettings.timeDisplay,
                onAlarmToggled = { context, alarm -> alarmListViewModel.toggleAlarm(context, alarm) },
                onAlarmDeleted = { context, alarm -> alarmListViewModel.cancelAndDeleteAlarm(context, alarm) },
                navigateToAlarmEditScreen = navigateToAlarmEditScreen,
                modifier = modifier
            )
        }
    }
}

@Composable
fun NotificationPermissionOff(
    promptPermission: ManagedActivityResultLauncher<String, Boolean>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Icon and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp)
            ) {
                // Icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Title
                Text(
                    text = stringResource(id = R.string.permission_required),
                    fontSize = 24.sp
                )
            }

            // Body
            Text(
                text = stringResource(id = R.string.permission_missing_notification),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp)
            )

            // Request Button
            Button(
                // TODO: API level check on Notification Permission
                onClick = { promptPermission.launch(Manifest.permission.POST_NOTIFICATIONS) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp)
            ) {
                Text(text = stringResource(id = R.string.permission_request))
            }
        }
    }
}

@Composable
fun AlarmListScreenContent(
    alarmList: List<Alarm>,
    timeDisplay: TimeDisplay,
    onAlarmToggled: (Context, Alarm) -> Unit,
    onAlarmDeleted: (Context, Alarm) -> Unit,
    navigateToAlarmEditScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Alarm List
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        if (alarmList.isNotEmpty()) {
            items(items = alarmList) { alarm ->
                AlarmCard(
                    alarm = alarm,
                    timeDisplay = timeDisplay,
                    onAlarmToggled = onAlarmToggled,
                    onAlarmDeleted = onAlarmDeleted,
                    navigateToAlarmEditScreen = navigateToAlarmEditScreen
                )
            }
        } else {
            item {
                NoAlarmsCard()
            }
        }
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmListScreenPreview() {
    AlarmScratchTheme {
        AlarmListScreenContent(
            alarmList = alarmSampleDataHardCodedIds,
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = { _, _ -> },
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun AlarmListScreenNoAlarmsPreview() {
    AlarmScratchTheme {
        AlarmListScreenContent(
            alarmList = emptyList(),
            timeDisplay = TimeDisplay.TwelveHour,
            onAlarmToggled = { _, _ -> },
            onAlarmDeleted = { _, _ -> },
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview
@Composable
private fun NotificationPermissionOffPreview() {
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    AlarmScratchTheme {
        NotificationPermissionOff(promptPermission = notificationPermissionLauncher)
    }
}

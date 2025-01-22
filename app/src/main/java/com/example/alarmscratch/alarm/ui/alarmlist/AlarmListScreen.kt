package com.example.alarmscratch.alarm.ui.alarmlist

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.component.AlarmCard
import com.example.alarmscratch.alarm.ui.alarmlist.component.NoAlarmsCard
import com.example.alarmscratch.core.ui.notificationcheck.NotificationChannelGateScreen
import com.example.alarmscratch.core.ui.notificationcheck.NotificationPermission
import com.example.alarmscratch.core.ui.permission.Permission
import com.example.alarmscratch.core.ui.permission.PermissionGateScreen
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

    val notificationGatedAlarmList: @Composable () -> Unit = {
        // Gate Alarm List Screen behind Alarm Notification Channel
        NotificationChannelGateScreen(
            notificationPermission = NotificationPermission.Alarm,
            gatedScreen = {
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
        )
    }

    // POST_NOTIFICATIONS permission requires API 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionGateScreen(
            permission = Permission.PostNotifications,
            gatedScreen = notificationGatedAlarmList,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        notificationGatedAlarmList()
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

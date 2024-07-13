package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.repository.AlarmListState
import com.example.alarmscratch.ui.alarmlist.AlarmListViewModel
import com.example.alarmscratch.ui.alarmlist.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmListScreen(
    navigateToAlarmEditScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    alarmListViewModel: AlarmListViewModel = viewModel(factory = AlarmListViewModel.Factory)
) {
    // State
    val alarmListState by alarmListViewModel.alarmList.collectAsState()

    // Actions
    val coroutineScope = rememberCoroutineScope()
    val onAlarmToggled: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { alarmListViewModel.updateAlarm(alarm) }
    }
    val onAlarmDeleted: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { alarmListViewModel.deleteAlarm(alarm) }
    }

    // The entire Alarm List Screen
    AlarmListScreenContent(
        alarmListState = alarmListState,
        onAlarmToggled = onAlarmToggled,
        onAlarmDeleted = onAlarmDeleted,
        navigateToAlarmEditScreen = navigateToAlarmEditScreen,
        modifier = modifier
    )
}

@Composable
fun AlarmListScreenContent(
    alarmListState: AlarmListState,
    onAlarmToggled: (Alarm) -> Unit,
    onAlarmDeleted: (Alarm) -> Unit,
    navigateToAlarmEditScreen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Alarm List
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        when (alarmListState) {
            is AlarmListState.Loading ->
                // TODO: Maybe show loading animation. Might be better to just have nothing,
                //  so you don't get a Loading Composable that just flashes on/off.
                Unit
            is AlarmListState.Success -> {
                if (alarmListState.alarmList.isNotEmpty()) {
                    // TODO: Look into key thing
                    items(items = alarmListState.alarmList, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm = alarm,
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
            is AlarmListState.Error ->
                // TODO: Do something for error
                Unit
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
            alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds),
            onAlarmToggled = {},
            onAlarmDeleted = {},
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
            alarmListState = AlarmListState.Success(emptyList()),
            onAlarmToggled = {},
            onAlarmDeleted = {},
            navigateToAlarmEditScreen = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

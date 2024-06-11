package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.repository.AlarmListState
import com.example.alarmscratch.ui.alarmlist.AlarmListViewModel
import com.example.alarmscratch.ui.alarmlist.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.BottomOceanBlue
import com.example.alarmscratch.ui.theme.TopOceanBlue
import kotlinx.coroutines.launch

@Composable
fun AlarmListScreen(
    modifier: Modifier = Modifier,
    alarmListViewModel: AlarmListViewModel = viewModel(factory = AlarmListViewModel.Factory)
) {
    val alarmListState by alarmListViewModel.alarmList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    // TODO: Temp inserting alarm here for now just for quick testing before the actual alarm creation screen is implemented
    val tempOnFabClicked: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { alarmListViewModel.insertAlarm(alarm) }
    }
    val onAlarmToggled: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { alarmListViewModel.updateAlarm(alarm) }
    }
    val onAlarmDeleted: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { alarmListViewModel.deleteAlarm(alarm) }
    }

    // The entire Alarm Screen
    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) {
        AlarmListScreenContent(
            alarmListState = alarmListState,
            onFabClicked = tempOnFabClicked,
            onAlarmToggled = onAlarmToggled,
            onAlarmDeleted = onAlarmDeleted
        )
    }
}

@Composable
fun AlarmListScreenContent(
    alarmListState: AlarmListState,
    onFabClicked: (Alarm) -> Unit,
    onAlarmToggled: (Alarm) -> Unit,
    onAlarmDeleted: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TopOceanBlue,
                        BottomOceanBlue
                    )
                )
            )
    ) {
        // Header
        SkylineHeader(alarmListState = alarmListState)

        // Alarm List
        AlarmCardList(
            alarmListState = alarmListState,
            onAlarmToggled = onAlarmToggled,
            onAlarmDeleted = onAlarmDeleted,
            modifier = Modifier
                .padding(top = 20.dp)
                .weight(1f)
        )

        // Floating Action Button
        LavaFloatingActionButton(
            onFabClicked = onFabClicked,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Navigation Bar
        VolcanoNavigationBar(
            selectedDestination = selectedDestination,
            onDestinationChange = { newDestination: Int -> selectedDestination = newDestination },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AlarmCardList(
    alarmListState: AlarmListState,
    onAlarmToggled: (Alarm) -> Unit,
    onAlarmDeleted: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
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
                            onAlarmDeleted = onAlarmDeleted
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

@Preview
@Composable
private fun AlarmListScreenPreview() {
    AlarmScratchTheme {
        AlarmListScreenContent(
            alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds),
            onFabClicked = {},
            onAlarmToggled = {},
            onAlarmDeleted = {}
        )
    }
}

@Preview
@Composable
private fun AlarmListScreenNoAlarmsPreview() {
    AlarmScratchTheme {
        AlarmListScreenContent(
            alarmListState = AlarmListState.Success(emptyList()),
            onFabClicked = {},
            onAlarmToggled = {},
            onAlarmDeleted = {}
        )
    }
}

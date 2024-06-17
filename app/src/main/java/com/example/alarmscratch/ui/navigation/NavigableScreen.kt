package com.example.alarmscratch.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.data.model.Alarm
import com.example.alarmscratch.data.repository.AlarmListState
import com.example.alarmscratch.ui.alarmlist.composable.AlarmListScreenContent
import com.example.alarmscratch.ui.alarmlist.composable.LavaFloatingActionButton
import com.example.alarmscratch.ui.alarmlist.composable.SkylineHeader
import com.example.alarmscratch.ui.alarmlist.composable.VolcanoNavigationBar
import com.example.alarmscratch.ui.alarmlist.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.BottomOceanBlue
import com.example.alarmscratch.ui.theme.TopOceanBlue
import kotlinx.coroutines.launch

@Composable
fun NavigableScreen(
    navigableScreenViewModel: NavigableScreenViewModel = viewModel(factory = NavigableScreenViewModel.Factory)
) {
    // Alarms
    val alarmListState by navigableScreenViewModel.alarmList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    // TODO: Temp inserting alarm here for now just for quick testing before the actual alarm creation screen is implemented
    val tempOnFabClicked: (Alarm) -> Unit = { alarm ->
        coroutineScope.launch { navigableScreenViewModel.insertAlarm(alarm) }
    }

    // Navigation
    val navController = rememberNavController()

    // Navigable Screen wrapping an Internal Screen
    NavigableScreenContent(
        navController = navController,
        alarmListState = alarmListState,
        onFabClicked = tempOnFabClicked
    ) {
        // Nested Internal Screen
        AlarmNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NavigableScreenContent(
    navController: NavController,
    alarmListState: AlarmListState,
    onFabClicked: (Alarm) -> Unit,
    internalScreen: @Composable () -> Unit
) {
    // Navigation
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedDestination by rememberSaveable(stateSaver = Destination.Saver) {
        mutableStateOf(
            value = Destination.ALL_DESTINATIONS.find { destination ->
                destination.route == currentBackStackEntry?.destination?.route
            } ?: Destination.AlarmList
        )
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
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
            SkylineHeader(
                currentScreen = selectedDestination,
                alarmListState = alarmListState
            )

            // Internal Screen
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f)
            ) {
                // Extracted this for Previews since they don't work with ViewModels
                internalScreen()
            }

            // Floating Action Button
            LavaFloatingActionButton(
                onFabClicked = onFabClicked,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Navigation Bar
            VolcanoNavigationBar(
                selectedDestination = selectedDestination.route,
                onDestinationChange = { newDestination: Destination -> selectedDestination = newDestination },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigableScreenAlarmListPreview() {
    AlarmScratchTheme {
        val alarmList = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)

        NavigableScreenContent(
            navController = rememberNavController(),
            alarmListState = alarmList,
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = alarmList,
                onAlarmToggled = {},
                onAlarmDeleted = {}
            )
        }
    }
}

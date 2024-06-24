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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.data.repository.AlarmListState
import com.example.alarmscratch.ui.alarmlist.composable.AlarmListScreenContent
import com.example.alarmscratch.ui.alarmlist.composable.LavaFloatingActionButton
import com.example.alarmscratch.ui.alarmlist.composable.SkylineHeader
import com.example.alarmscratch.ui.alarmlist.composable.VolcanoNavigationBar
import com.example.alarmscratch.ui.alarmlist.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.ui.settings.SettingsScreen
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.BottomOceanBlue
import com.example.alarmscratch.ui.theme.TopOceanBlue

@Composable
fun NavigableScreen(
    rootNavHostController: NavHostController,
    navigableScreenViewModel: NavigableScreenViewModel = viewModel(factory = NavigableScreenViewModel.Factory)
) {
    // State
    val alarmListState by navigableScreenViewModel.alarmList.collectAsState()

    // Actions
    val onFabClicked: () -> Unit = {
        rootNavHostController.navigateSingleTop(Destination.AlarmCreation.route)
    }

    // Navigation
    val localNavHostController = rememberNavController()

    // Navigable Screen wrapping an Internal Screen
    NavigableScreenContent(
        localNavHostController = localNavHostController,
        alarmListState = alarmListState,
        onFabClicked = onFabClicked
    ) {
        // Nested Internal Screen
        AlarmNavHost(
            localNavHostController = localNavHostController,
            rootNavHostController = rootNavHostController,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun NavigableScreenContent(
    localNavHostController: NavHostController,
    alarmListState: AlarmListState,
    onFabClicked: () -> Unit,
    internalScreen: @Composable () -> Unit
) {
    // Navigation
    val currentBackStackEntry by localNavHostController.currentBackStackEntryAsState()
    val selectedDestination = Destination.ALL_DESTINATIONS.find { destination ->
        destination.route == currentBackStackEntry?.destination?.route
    } ?: Destination.AlarmList

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
            Box(modifier = Modifier.weight(1f)) {
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
                onDestinationChange = { localNavHostController.navigateSingleTop(it.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun NavigableScreenAlarmListPreview() {
    AlarmScratchTheme {
        val alarmList = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)

        NavigableScreenContent(
            localNavHostController = rememberNavController(),
            alarmListState = alarmList,
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = alarmList,
                onAlarmToggled = {},
                onAlarmDeleted = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NavigableScreenAlarmListNoAlarmsPreview() {
    AlarmScratchTheme {
        val alarmList = AlarmListState.Success(alarmList = emptyList())

        NavigableScreenContent(
            localNavHostController = rememberNavController(),
            alarmListState = alarmList,
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = alarmList,
                onAlarmToggled = {},
                onAlarmDeleted = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NavigableScreenSettingsPreview() {
    AlarmScratchTheme {
        val navHostController = rememberNavController()

        NavigableScreenContent(
            localNavHostController = navHostController,
            alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds),
            onFabClicked = {}
        ) {
            SettingsScreen(
                navHostController = navHostController,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

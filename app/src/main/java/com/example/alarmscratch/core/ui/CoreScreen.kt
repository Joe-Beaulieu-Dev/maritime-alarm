package com.example.alarmscratch.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreenContent
import com.example.alarmscratch.core.navigation.ALL_DESTINATIONS
import com.example.alarmscratch.core.navigation.AlarmCreation
import com.example.alarmscratch.core.navigation.AlarmList
import com.example.alarmscratch.core.navigation.AlarmNavHost
import com.example.alarmscratch.core.navigation.navigateSingleTop
import com.example.alarmscratch.core.ui.component.LavaFloatingActionButton
import com.example.alarmscratch.core.ui.component.SkylineHeader
import com.example.alarmscratch.core.ui.component.VolcanoNavigationBar
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BottomOceanBlue
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.settings.SettingsScreen

@Composable
fun CoreScreen(rootNavHostController: NavHostController) {
    // Actions
    val onFabClicked: () -> Unit = { rootNavHostController.navigateSingleTop(AlarmCreation.route) }

    // Navigation
    val localNavHostController = rememberNavController()

    // Core Screen wrapping an Internal Screen
    CoreScreenContent(
        localNavHostController = localNavHostController,
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
fun CoreScreenContent(
    localNavHostController: NavHostController,
    onFabClicked: () -> Unit,
    internalScreen: @Composable () -> Unit
) {
    // Navigation
    val currentBackStackEntry by localNavHostController.currentBackStackEntryAsState()
    val selectedDestination = ALL_DESTINATIONS.find { destination ->
        destination.route == currentBackStackEntry?.destination?.route
    } ?: AlarmList

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
            SkylineHeader(currentScreen = selectedDestination)

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

/*
 * Previews
 */

@Preview
@Composable
private fun CoreScreenAlarmListPreview() {
    AlarmScratchTheme {
        CoreScreenContent(
            localNavHostController = rememberNavController(),
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds),
                onAlarmToggled = {},
                onAlarmDeleted = {},
                navigateToAlarmEditScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CoreScreenAlarmListNoAlarmsPreview() {
    AlarmScratchTheme {
        CoreScreenContent(
            localNavHostController = rememberNavController(),
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = AlarmListState.Success(alarmList = emptyList()),
                onAlarmToggled = {},
                onAlarmDeleted = {},
                navigateToAlarmEditScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CoreScreenSettingsPreview() {
    AlarmScratchTheme {
        val navHostController = rememberNavController()

        CoreScreenContent(
            localNavHostController = navHostController,
            onFabClicked = {}
        ) {
            SettingsScreen(
                navHostController = navHostController,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

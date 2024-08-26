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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreenContent
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.core.navigation.AlarmListScreen
import com.example.alarmscratch.core.navigation.AlarmNavHost
import com.example.alarmscratch.core.navigation.NavComponent
import com.example.alarmscratch.core.navigation.SettingsScreen
import com.example.alarmscratch.core.ui.component.LavaFloatingActionButton
import com.example.alarmscratch.core.ui.component.SkylineHeader
import com.example.alarmscratch.core.ui.component.SkylineHeaderContent
import com.example.alarmscratch.core.ui.component.VolcanoNavigationBar
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BottomOceanBlue
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.settings.SettingsScreen

@Composable
fun CoreScreen(
    rootNavHostController: NavHostController,
    navigateToAlarmCreationScreen: () -> Unit
) {
    // Navigation
    val localNavHostController = rememberNavController()
    val currentBackStackEntry by localNavHostController.currentBackStackEntryAsState()
    val selectedNavComponentDest = NavComponent.entries.find { navComponent ->
        currentBackStackEntry?.destination?.hasRoute(navComponent.destination::class) ?: false
    }?.destination ?: NavComponent.ALARM_LIST_NAV_COMPONENT.destination

    // Core Screen wrapping an Internal Screen
    CoreScreenContent(
        header = { SkylineHeader(selectedNavComponentDest = selectedNavComponentDest) },
        onFabClicked = navigateToAlarmCreationScreen,
        navigationBar = {
            VolcanoNavigationBar(
                selectedNavComponentDest = selectedNavComponentDest,
                onDestinationChange = { destination -> localNavHostController.navigateSingleTop(destination) },
                modifier = Modifier.fillMaxWidth()
            )
        }
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
    header: @Composable () -> Unit,
    onFabClicked: () -> Unit,
    navigationBar: @Composable () -> Unit,
    internalScreen: @Composable () -> Unit
) {
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
            header()

            // Internal Screen
            Box(modifier = Modifier.weight(1f)) {
                internalScreen()
            }

            // Floating Action Button
            LavaFloatingActionButton(
                onFabClicked = onFabClicked,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Navigation Bar
            navigationBar()
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
        val selectedNavComponentDest = AlarmListScreen
        val alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)

        CoreScreenContent(
            header = {
                SkylineHeaderContent(
                    selectedNavComponentDest = selectedNavComponentDest,
                    alarmListState = alarmListState
                )
            },
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = alarmListState,
                onAlarmToggled = { _, _ -> },
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
        val selectedNavComponentDest = AlarmListScreen
        val alarmListState = AlarmListState.Success(alarmList = emptyList())

        CoreScreenContent(
            header = {
                SkylineHeaderContent(
                    selectedNavComponentDest = selectedNavComponentDest,
                    alarmListState = alarmListState
                )
            },
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onFabClicked = {}
        ) {
            AlarmListScreenContent(
                alarmListState = alarmListState,
                onAlarmToggled = { _, _ -> },
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
        val selectedNavComponentDest = SettingsScreen

        CoreScreenContent(
            header = {
                SkylineHeaderContent(
                    selectedNavComponentDest = selectedNavComponentDest,
                    alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)
                )
            },
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onFabClicked = {}
        ) {
            SettingsScreen(
                navigateToAlarmDefaultsScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

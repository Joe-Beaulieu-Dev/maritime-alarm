package com.example.alarmscratch.core.ui.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreenContent
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.core.extension.toCountdownString
import com.example.alarmscratch.core.navigation.AlarmNavHost
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.navigation.NavComponent
import com.example.alarmscratch.core.ui.core.component.AlarmCountdownState
import com.example.alarmscratch.core.ui.core.component.LavaFloatingActionButton
import com.example.alarmscratch.core.ui.core.component.NextAlarmCloudContent
import com.example.alarmscratch.core.ui.core.component.SkylineHeader
import com.example.alarmscratch.core.ui.core.component.SkylineHeaderContent
import com.example.alarmscratch.core.ui.core.component.VolcanoNavigationBar
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BottomOceanBlue
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.SettingsScreen
import com.example.alarmscratch.settings.data.model.TimeDisplay

@Composable
fun CoreScreen(
    rootNavHostController: NavHostController,
    navigateToAlarmCreationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Configure Status Bar
    StatusBarUtil.setLightStatusBar()

    // Navigation
    val localNavHostController = rememberNavController()
    val currentBackStackEntry by localNavHostController.currentBackStackEntryAsState()
    val selectedNavComponentDest = NavComponent.entries.find { navComponent ->
        currentBackStackEntry?.destination?.hasRoute(navComponent.destination::class) ?: false
    }?.destination ?: NavComponent.ALARM_LIST.destination

    // Core Screen wrapping an Internal Screen
    CoreScreenContent(
        modifier = modifier,
        selectedNavComponentDest = selectedNavComponentDest,
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
    modifier: Modifier = Modifier,
    selectedNavComponentDest: Destination,
    header: @Composable () -> Unit,
    onFabClicked: () -> Unit,
    navigationBar: @Composable () -> Unit,
    internalScreen: @Composable () -> Unit
) {
    // LavaFloatingActionButton specs
    val fabHeight = 70.dp
    val volcanoSpacerHeight = 6.dp
    val fabAnimationHeight = with(LocalDensity.current) { (fabHeight + volcanoSpacerHeight).toPx().toInt() }

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .background(color = SkyBlue)
            .windowInsetsPadding(WindowInsets.systemBars)
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

            // LavaFloatingActionButton with Slide In/Out Animation
            AnimatedVisibility(
                visible = selectedNavComponentDest == Destination.AlarmListScreen,
                enter = slideInVertically(
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
                    initialOffsetY = { fabAnimationHeight }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing),
                    targetOffsetY = { fabAnimationHeight }
                )
            ) {
                LavaFloatingActionButton(
                    enabled = selectedNavComponentDest == Destination.AlarmListScreen,
                    onFabClicked = onFabClicked
                )
            }
            Spacer(modifier = Modifier.height(volcanoSpacerHeight))

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
    val selectedNavComponentDest = Destination.AlarmListScreen
    val alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)

    AlarmScratchTheme {
        CoreScreenContent(
            selectedNavComponentDest = selectedNavComponentDest,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            selectedNavComponentDest = selectedNavComponentDest,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.Alarm,
                                countdownText = alarmListState.alarmList.first().toCountdownString(LocalContext.current)
                            ),
                            timeChangeReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {}
                            }
                        )
                    }
                )
            },
            onFabClicked = {},
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) {
            AlarmListScreenContent(
                alarmList = alarmListState.alarmList,
                timeDisplay = TimeDisplay.TwelveHour,
                onAlarmToggled = { _, _ -> },
                onAlarmDeleted = { _, _ -> },
                navigateToAlarmEditScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CoreScreenAlarmListNoAlarmsPreview() {
    val selectedNavComponentDest = Destination.AlarmListScreen

    AlarmScratchTheme {
        CoreScreenContent(
            selectedNavComponentDest = selectedNavComponentDest,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            selectedNavComponentDest = selectedNavComponentDest,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.AlarmOff,
                                countdownText = stringResource(id = R.string.no_active_alarms)
                            ),
                            timeChangeReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {}
                            }
                        )
                    }
                )
            },
            onFabClicked = {},
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) {
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
}

@Preview
@Composable
private fun CoreScreenSettingsPreview() {
    val selectedNavComponentDest = Destination.SettingsScreen

    AlarmScratchTheme {
        CoreScreenContent(
            selectedNavComponentDest = selectedNavComponentDest,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            selectedNavComponentDest = selectedNavComponentDest,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.Alarm,
                                countdownText = alarmSampleDataHardCodedIds.first().toCountdownString(LocalContext.current)
                            ),
                            timeChangeReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {}
                            }
                        )
                    }
                )
            },
            onFabClicked = {},
            navigationBar = {
                VolcanoNavigationBar(
                    selectedNavComponentDest = selectedNavComponentDest,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) {
            SettingsScreen(
                navigateToGeneralSettingsScreen = {},
                navigateToAlarmDefaultsScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

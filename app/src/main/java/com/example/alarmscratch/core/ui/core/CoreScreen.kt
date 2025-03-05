package com.example.alarmscratch.core.ui.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.alarmSampleDataHardCodedIds
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreenContent
import com.example.alarmscratch.core.extension.getAndRemoveStringFromBackStack
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.core.extension.toCountdownString
import com.example.alarmscratch.core.navigation.CoreNavHost
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.navigation.NavComponent
import com.example.alarmscratch.core.runtime.ObserveAsEvent
import com.example.alarmscratch.core.ui.core.component.AlarmCountdownState
import com.example.alarmscratch.core.ui.core.component.LavaFloatingActionButton
import com.example.alarmscratch.core.ui.core.component.NextAlarmCloudContent
import com.example.alarmscratch.core.ui.core.component.SkylineHeader
import com.example.alarmscratch.core.ui.core.component.SkylineHeaderContent
import com.example.alarmscratch.core.ui.core.component.VolcanoNavigationBar
import com.example.alarmscratch.core.ui.core.component.VolcanoWithLava
import com.example.alarmscratch.core.ui.snackbar.SnackbarEvent
import com.example.alarmscratch.core.ui.snackbar.global.GlobalSnackbarController
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.BottomOceanBlue
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.SettingsScreen
import com.example.alarmscratch.settings.data.model.TimeDisplay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun CoreScreen(
    secondaryNavHostController: NavHostController,
    navigateToAlarmCreationScreen: () -> Unit,
    modifier: Modifier = Modifier,
    coreScreenViewModel: CoreScreenViewModel = viewModel(factory = CoreScreenViewModel.Factory)
) {
    // Configure Status Bar
    StatusBarUtil.setLightStatusBar()

    // Navigation
    val coreNavHostController = rememberNavController()
    val currentCoreBackStackEntry by coreNavHostController.currentBackStackEntryAsState()
    val currentCoreDestination = NavComponent.fromNavBackStackEntry(currentCoreBackStackEntry)
    var previousCoreDestination by remember { mutableStateOf(currentCoreDestination) }
    val setPreviousCoreDestination: (Destination) -> Unit = { previousCoreDestination = it }

    // Track that the User is leaving the Core Screen by
    // setting previousCoreDestination to currentCoreDestination
    val currentSecondaryBackStackEntry by secondaryNavHostController.currentBackStackEntryAsState()
    LaunchedEffect(key1 = currentSecondaryBackStackEntry) {
        if (currentSecondaryBackStackEntry != null) {
            setPreviousCoreDestination(currentCoreDestination)
        }
    }

    // Core Screen wrapping an Internal Screen
    CoreScreenContent(
        modifier = modifier,
        currentCoreDestination = currentCoreDestination,
        previousCoreDestination = previousCoreDestination,
        header = {
            SkylineHeader(
                currentCoreDestination = currentCoreDestination,
                previousCoreDestination = previousCoreDestination
            )
        },
        onFabClicked = navigateToAlarmCreationScreen,
        navigationBar = {
            VolcanoNavigationBar(
                currentCoreDestination = currentCoreDestination,
                onDestinationChange = { newDestination ->
                    setPreviousCoreDestination(currentCoreDestination)
                    coreNavHostController.navigateSingleTop(newDestination)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        localSnackbarFlow = coreScreenViewModel.localSnackbarFlow,
        retrieveSnackbarFromPrevious = {
            coreScreenViewModel.retrieveSnackbarFromPrevious(
                secondaryNavHostController.getAndRemoveStringFromBackStack(SnackbarEvent.KEY_SNACKBAR_EVENT_MESSAGE)
            )
        }
    ) {
        // Nested Internal Screen
        CoreNavHost(
            coreNavHostController = coreNavHostController,
            secondaryNavHostController = secondaryNavHostController,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun CoreScreenContent(
    modifier: Modifier = Modifier,
    currentCoreDestination: Destination,
    previousCoreDestination: Destination,
    header: @Composable () -> Unit,
    onFabClicked: () -> Unit,
    navigationBar: @Composable () -> Unit,
    localSnackbarFlow: Flow<SnackbarEvent>,
    retrieveSnackbarFromPrevious: () -> Unit,
    internalScreen: @Composable () -> Unit
) {
    // Specs
    val volcanoSpacerHeight = 6.dp

    // Snackbar
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // Snackbar from previous screen
    LaunchedEffect(key1 = Unit) {
        // Update the ViewModel with the Snackbar message from the previous screen (Alarm Create/Edit)
        retrieveSnackbarFromPrevious()
    }
    ObserveAsEvent(flow = localSnackbarFlow) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = event.message)
        }
    }
    // Snackbar from nested screen
    ObserveAsEvent(flow = GlobalSnackbarController.snackbarFlow) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = event.message)
        }
    }

    Scaffold(
        bottomBar = navigationBar,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = VolcanicRock,
                    contentColor = BoatSails
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    0.07f to SkyBlue,
                    0.08f to TopOceanBlue,
                    1.0f to BottomOceanBlue
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Header
            header()

            // Internal Screen
            Box(modifier = Modifier.weight(1f)) {
                internalScreen()
            }

            // LavaFloatingActionButton with Slide In/Out Animation
            LavaFloatingActionButton(
                currentCoreDestination = currentCoreDestination,
                previousCoreDestination = previousCoreDestination,
                onFabClicked = onFabClicked,
                volcanoSpacerHeight = volcanoSpacerHeight
            )
            Spacer(modifier = Modifier.height(volcanoSpacerHeight))

            // Volcano
            VolcanoWithLava()
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun CoreScreenAlarmListPreview() {
    val currentCoreDestination = Destination.AlarmListScreen
    val alarmListState = AlarmListState.Success(alarmList = alarmSampleDataHardCodedIds)

    AlarmScratchTheme {
        CoreScreenContent(
            currentCoreDestination = currentCoreDestination,
            previousCoreDestination = Destination.AlarmListScreen,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            currentCoreDestination = currentCoreDestination,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.Alarm,
                                countdownText = alarmListState.alarmList.first().toCountdownString(LocalContext.current)
                            ),
                            visibleState = MutableTransitionState(true),
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
                    currentCoreDestination = currentCoreDestination,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            localSnackbarFlow = Channel<SnackbarEvent>().receiveAsFlow(),
            retrieveSnackbarFromPrevious = {}
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
    val currentCoreDestination = Destination.AlarmListScreen

    AlarmScratchTheme {
        CoreScreenContent(
            currentCoreDestination = currentCoreDestination,
            previousCoreDestination = Destination.AlarmListScreen,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            currentCoreDestination = currentCoreDestination,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.AlarmOff,
                                countdownText = stringResource(id = R.string.no_active_alarms)
                            ),
                            visibleState = MutableTransitionState(true),
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
                    currentCoreDestination = currentCoreDestination,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            localSnackbarFlow = Channel<SnackbarEvent>().receiveAsFlow(),
            retrieveSnackbarFromPrevious = {}
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
    val currentCoreDestination = Destination.SettingsScreen

    AlarmScratchTheme {
        CoreScreenContent(
            currentCoreDestination = currentCoreDestination,
            previousCoreDestination = Destination.AlarmListScreen,
            header = {
                SkylineHeaderContent(
                    nextAlarmIndicator = {
                        NextAlarmCloudContent(
                            currentCoreDestination = currentCoreDestination,
                            alarmCountdownState = AlarmCountdownState.Success(
                                icon = Icons.Default.Alarm,
                                countdownText = alarmSampleDataHardCodedIds.first().toCountdownString(LocalContext.current)
                            ),
                            visibleState = MutableTransitionState(false),
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
                    currentCoreDestination = currentCoreDestination,
                    onDestinationChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            localSnackbarFlow = Channel<SnackbarEvent>().receiveAsFlow(),
            retrieveSnackbarFromPrevious = {}
        ) {
            SettingsScreen(
                navigateToGeneralSettingsScreen = {},
                navigateToAlarmDefaultsScreen = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

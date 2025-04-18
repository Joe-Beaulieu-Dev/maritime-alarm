package com.joebsource.lavalarm.core.ui.core

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.joebsource.lavalarm.R
import com.joebsource.lavalarm.alarm.data.preview.alarmSampleDataHardCodedIds
import com.joebsource.lavalarm.alarm.data.repository.AlarmListState
import com.joebsource.lavalarm.alarm.ui.alarmlist.AlarmListScreenContent
import com.joebsource.lavalarm.core.extension.getAndRemoveStringFromBackStack
import com.joebsource.lavalarm.core.extension.navigateSingleTop
import com.joebsource.lavalarm.core.extension.toCountdownString
import com.joebsource.lavalarm.core.navigation.CoreScreenNavComponent
import com.joebsource.lavalarm.core.navigation.CoreScreenNavHost
import com.joebsource.lavalarm.core.navigation.Destination
import com.joebsource.lavalarm.core.runtime.ObserveAsEvent
import com.joebsource.lavalarm.core.ui.core.component.AlarmCountdownState
import com.joebsource.lavalarm.core.ui.core.component.LavaFloatingActionButton
import com.joebsource.lavalarm.core.ui.core.component.NextAlarmCloudContent
import com.joebsource.lavalarm.core.ui.core.component.SkylineHeader
import com.joebsource.lavalarm.core.ui.core.component.SkylineHeaderContent
import com.joebsource.lavalarm.core.ui.core.component.VolcanoNavigationBar
import com.joebsource.lavalarm.core.ui.core.component.VolcanoWithLava
import com.joebsource.lavalarm.core.ui.snackbar.SnackbarEvent
import com.joebsource.lavalarm.core.ui.snackbar.global.GlobalSnackbarController
import com.joebsource.lavalarm.core.ui.theme.BoatSails
import com.joebsource.lavalarm.core.ui.theme.BottomOceanBlue
import com.joebsource.lavalarm.core.ui.theme.DarkVolcanicRock
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.SkyBlue
import com.joebsource.lavalarm.core.ui.theme.TopOceanBlue
import com.joebsource.lavalarm.core.ui.theme.VolcanicRock
import com.joebsource.lavalarm.core.util.StatusBarUtil
import com.joebsource.lavalarm.settings.SettingsScreen
import com.joebsource.lavalarm.settings.data.model.TimeDisplay
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
    // Current and previous Destinations must be tracked in order to prevent LavaFloatingActionButton,
    // NextAlarmCloud, and VolcanoNavigationBar from unnecessarily reanimating on orientation change, etc.
    // Part of achieving this requires tracking the previous Destination during CoreScreen (coreNavHostController)
    // navigation. Simply observing coreNavHostController.previousBackStackEntry is not sufficient
    // because once the User pops the entire back stack (is back at the original Destination),
    // there will be no "previous Destination" in the back stack, even though there actually was
    // a previous Destination in practice. Because of this, the previous Destination must be tracked manually.
    val coreNavHostController = rememberNavController()
    val currentCoreDestination by coreScreenViewModel.currentCoreDestination.collectAsState()
    val previousCoreDestination by coreScreenViewModel.previousCoreDestination.collectAsState()
    val setCurrentCoreDestination: (Destination) -> Unit = coreScreenViewModel::setCurrentCoreDestination
    val setPreviousCoreDestination: (Destination) -> Unit = coreScreenViewModel::setPreviousCoreDestination

    // Back press
    val activity: Activity? = (LocalContext.current as? Activity)
    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    if (activity != null && onBackPressedDispatcher != null) {
        onBackPressedDispatcher.addCallback(owner = LocalLifecycleOwner.current) {
            // set previousCoreDestination before navigating back
            setPreviousCoreDestination(currentCoreDestination)
            // Navigate back
            // Popping the back stack will have no effect if it's empty.
            // Standard back press functionality in this scenario is to finish the Activity.
            val isBackStackEmpty = !coreNavHostController.popBackStack()
            if (isBackStackEmpty) {
                activity.finish()
            }
        }
    }

    // Track changes in coreNavHostController's back stack
    // Don't use delegation, and also grab the value from the back stack state. This is in order
    // to avoid smart casting issues with delegated properties and open/custom getters.
    val currentCoreBackStackEntry = coreNavHostController.currentBackStackEntryAsState().value
    if (currentCoreBackStackEntry != null) {
        LaunchedEffect(key1 = currentCoreBackStackEntry.destination) {
            setCurrentCoreDestination(CoreScreenNavComponent.fromNavBackStackEntry(currentCoreBackStackEntry))
        }
    }

    // Track that the User is leaving the CoreScreen by setting previousCoreDestination to
    // currentCoreDestination when the secondaryNavHostController's NavBackStackEntry changes.
    // Don't use delegation, and also grab the value from the back stack state. This is in order
    // to avoid smart casting issues with delegated properties and open/custom getters.
    val currentSecondaryBackStackEntry = secondaryNavHostController.currentBackStackEntryAsState().value
    if (currentSecondaryBackStackEntry != null) {
        LaunchedEffect(key1 = currentSecondaryBackStackEntry.destination) {
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
                    // previousCoreDestination needs to be updated on bottom navigation
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
        CoreScreenNavHost(
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
        topBar = header,
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
                    0.93f to BottomOceanBlue,
                    0.94f to DarkVolcanicRock
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
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

    LavalarmTheme {
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

    LavalarmTheme {
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

    LavalarmTheme {
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

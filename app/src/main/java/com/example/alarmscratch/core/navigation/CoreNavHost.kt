package com.example.alarmscratch.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreen
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.settings.SettingsScreen

@Composable
fun AlarmNavHost(
    localNavHostController: NavHostController,
    rootNavHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = localNavHostController,
        startDestination = Destination.AlarmListScreen,
        modifier = modifier
    ) {
        // Alarm List Screen
        composable<Destination.AlarmListScreen> {
            AlarmListScreen(
                navigateToAlarmEditScreen = { alarmId ->
                    rootNavHostController.navigateSingleTop(Destination.AlarmEditScreen(alarmId = alarmId))
                }
            )
        }

        // Settings Screen
        composable<Destination.SettingsScreen> {
            SettingsScreen(
                navigateToGeneralSettingsScreen = { rootNavHostController.navigateSingleTop(Destination.GeneralSettingsScreen) },
                navigateToAlarmDefaultsScreen = { rootNavHostController.navigateSingleTop(Destination.AlarmDefaultsScreen) }
            )
        }
    }
}

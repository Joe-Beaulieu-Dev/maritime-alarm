package com.example.alarmscratch.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreen
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.settings.SettingsScreen

@Composable
fun CoreNavHost(
    coreNavHostController: NavHostController,
    secondaryNavHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = coreNavHostController,
        startDestination = Destination.AlarmListScreen,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 400))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 400))
        },
        modifier = modifier
    ) {
        // Alarm List Screen
        composable<Destination.AlarmListScreen> {
            AlarmListScreen(
                navigateToAlarmEditScreen = { alarmId ->
                    secondaryNavHostController.navigateSingleTop(Destination.AlarmEditScreen(alarmId = alarmId))
                }
            )
        }

        // Settings Screen
        composable<Destination.SettingsScreen> {
            SettingsScreen(
                navigateToGeneralSettingsScreen = { secondaryNavHostController.navigateSingleTop(Destination.GeneralSettingsScreen) },
                navigateToAlarmDefaultsScreen = { secondaryNavHostController.navigateSingleTop(Destination.AlarmDefaultsScreen) }
            )
        }
    }
}

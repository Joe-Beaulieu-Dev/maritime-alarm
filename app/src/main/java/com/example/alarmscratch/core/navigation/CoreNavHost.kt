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
        startDestination = AlarmListScreen.route,
        modifier = modifier
    ) {
        composable(route = AlarmListScreen.route) {
            AlarmListScreen(navigateToAlarmEditScreen = { alarmId ->
                rootNavHostController.navigateSingleTop("${AlarmEditScreen.route}/$alarmId")
            })
        }
        composable(route = SettingsScreen.route) {
            SettingsScreen(navHostController = rootNavHostController)
        }
    }
}

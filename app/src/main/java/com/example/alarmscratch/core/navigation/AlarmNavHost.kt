package com.example.alarmscratch.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alarmscratch.alarm.ui.alarmlist.AlarmListScreen
import com.example.alarmscratch.settings.SettingsScreen

@Composable
fun AlarmNavHost(
    localNavHostController: NavHostController,
    rootNavHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = localNavHostController,
        startDestination = AlarmList.route,
        modifier = modifier
    ) {
        composable(route = AlarmList.route) {
            AlarmListScreen(navigateToAlarmEditScreen = { alarmId ->
                rootNavHostController.navigateSingleTop("${AlarmEdit.route}/$alarmId")
            })
        }
        composable(route = Settings.route) {
            SettingsScreen(navHostController = rootNavHostController)
        }
    }
}

fun NavHostController.navigateSingleTop(route: String) =
    navigate(route) {
        launchSingleTop = true
    }

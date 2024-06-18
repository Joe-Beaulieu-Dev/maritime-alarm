package com.example.alarmscratch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alarmscratch.ui.alarmlist.composable.AlarmListScreen
import com.example.alarmscratch.ui.settings.SettingsScreen

@Composable
fun AlarmNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.AlarmList.route,
        modifier = modifier
    ) {
        composable(route = Destination.AlarmList.route) {
            AlarmListScreen()
        }
        composable(route = Destination.Settings.route) {
            SettingsScreen()
        }
    }
}

fun NavHostController.navigateSingleTop(route: String) =
    navigate(route) {
        launchSingleTop = true
    }

package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.ui.navigation.Destination
import com.example.alarmscratch.ui.navigation.NavigableScreen
import com.example.alarmscratch.ui.settings.AlarmDefaultsScreen

@Composable
fun AlarmApp() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = Destination.NavigableScreen.route
    ) {
        composable(route = Destination.NavigableScreen.route) {
            NavigableScreen(rootNavHostController = navHostController)
        }
        composable(route = Destination.AlarmDefaultSettings.route) {
            AlarmDefaultsScreen()
        }
    }
}

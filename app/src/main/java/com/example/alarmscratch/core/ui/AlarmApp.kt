package com.example.alarmscratch.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.ui.alarmcreate.AlarmCreationScreen
import com.example.alarmscratch.alarm.ui.alarmedit.AlarmEditScreen
import com.example.alarmscratch.core.navigation.AlarmCreation
import com.example.alarmscratch.core.navigation.AlarmDefaultSettings
import com.example.alarmscratch.core.navigation.AlarmEdit
import com.example.alarmscratch.core.navigation.NavigableScreen
import com.example.alarmscratch.settings.AlarmDefaultsScreen

@Composable
fun AlarmApp() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = NavigableScreen.route
    ) {
        composable(route = NavigableScreen.route) {
            NavigableScreen(rootNavHostController = navHostController)
        }
        composable(route = AlarmCreation.route) {
            AlarmCreationScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AlarmEdit.routeWithArgs,
            arguments = AlarmEdit.args
        ) {
            AlarmEditScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = AlarmDefaultSettings.route) {
            AlarmDefaultsScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.ui.alarmcreation.AlarmCreationScreen
import com.example.alarmscratch.ui.alarmedit.AlarmEditScreen
import com.example.alarmscratch.ui.navigation.AlarmCreation
import com.example.alarmscratch.ui.navigation.AlarmDefaultSettings
import com.example.alarmscratch.ui.navigation.AlarmEdit
import com.example.alarmscratch.ui.navigation.NavigableScreen
import com.example.alarmscratch.ui.settings.AlarmDefaultsScreen

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

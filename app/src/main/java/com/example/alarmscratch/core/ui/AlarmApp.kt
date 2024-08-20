package com.example.alarmscratch.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.ui.alarmcreate.AlarmCreationScreen
import com.example.alarmscratch.alarm.ui.alarmedit.AlarmEditScreen
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.core.navigation.AlarmCreationScreen
import com.example.alarmscratch.core.navigation.AlarmDefaultsScreen
import com.example.alarmscratch.core.navigation.AlarmEditScreen
import com.example.alarmscratch.core.navigation.CoreScreen
import com.example.alarmscratch.core.navigation.RingtonePickerScreen
import com.example.alarmscratch.settings.AlarmDefaultsScreen

@Composable
fun AlarmApp() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = CoreScreen.route
    ) {
        composable(route = CoreScreen.route) {
            CoreScreen(rootNavHostController = navHostController)
        }
        composable(route = AlarmCreationScreen.route) {
            AlarmCreationScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { navHostController.navigateSingleTop(RingtonePickerScreen.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AlarmEditScreen.routeWithArgs,
            arguments = AlarmEditScreen.args
        ) {
            AlarmEditScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { navHostController.navigateSingleTop(RingtonePickerScreen.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = RingtonePickerScreen.route) {
            RingtonePickerScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = AlarmDefaultsScreen.route) {
            AlarmDefaultsScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

package com.example.alarmscratch.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.ui.alarmcreate.AlarmCreationScreen
import com.example.alarmscratch.alarm.ui.alarmedit.AlarmEditScreen
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
        startDestination = CoreScreen
    ) {
        // Core Screen
        composable<CoreScreen> {
            CoreScreen(
                rootNavHostController = navHostController,
                navigateToAlarmCreationScreen = { navHostController.navigate(AlarmCreationScreen) { launchSingleTop = true } }
            )
        }

        // Alarm Creation Screen
        composable<AlarmCreationScreen> {
            AlarmCreationScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUriString ->
                    navHostController.navigate(RingtonePickerScreen(ringtoneUriString = ringtoneUriString)) { launchSingleTop = true }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Edit Screen
        composable<AlarmEditScreen> {
            AlarmEditScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUriString ->
                    navHostController.navigate(RingtonePickerScreen(ringtoneUriString = ringtoneUriString)) { launchSingleTop = true }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Ringtone Picker Screen
        composable<RingtonePickerScreen> {
            RingtonePickerScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Defaults Screen
        composable<AlarmDefaultsScreen> {
            AlarmDefaultsScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

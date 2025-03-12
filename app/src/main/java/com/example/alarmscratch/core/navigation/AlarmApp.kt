package com.example.alarmscratch.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.alarm.ui.alarmcreate.AlarmCreationScreen
import com.example.alarmscratch.alarm.ui.alarmedit.AlarmEditScreen
import com.example.alarmscratch.core.extension.navigateSingleTop
import com.example.alarmscratch.core.ui.core.CoreScreen
import com.example.alarmscratch.core.ui.ringtonepicker.RingtonePickerScreen
import com.example.alarmscratch.settings.ui.alarmdefaults.AlarmDefaultsScreen
import com.example.alarmscratch.settings.ui.generalsettings.GeneralSettingsScreen

@Composable
fun AlarmApp() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = Destination.CoreScreen
    ) {
        // Core Screen
        composable<Destination.CoreScreen> {
            CoreScreen(
                secondaryNavHostController = navHostController,
                navigateToAlarmCreationScreen = { navHostController.navigateSingleTop(Destination.AlarmCreationScreen) },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Creation Screen
        composable<Destination.AlarmCreationScreen> {
            AlarmCreationScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUri ->
                    navHostController.navigateSingleTop(Destination.RingtonePickerScreen(ringtoneUri = ringtoneUri))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Edit Screen
        composable<Destination.AlarmEditScreen> {
            AlarmEditScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUri ->
                    navHostController.navigateSingleTop(Destination.RingtonePickerScreen(ringtoneUri = ringtoneUri))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Ringtone Picker Screen
        composable<Destination.RingtonePickerScreen> {
            RingtonePickerScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }

        // General Settings Screen
        composable<Destination.GeneralSettingsScreen> {
            GeneralSettingsScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Defaults Screen
        composable<Destination.AlarmDefaultsScreen> {
            AlarmDefaultsScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUri ->
                    navHostController.navigateSingleTop(Destination.RingtonePickerScreen(ringtoneUri = ringtoneUri))
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

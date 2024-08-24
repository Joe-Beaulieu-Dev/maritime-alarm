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
import com.example.alarmscratch.core.navigation.CoreNavComponent2
import com.example.alarmscratch.core.navigation.CoreScreen
import com.example.alarmscratch.core.navigation.DestinationNavType
import com.example.alarmscratch.core.navigation.RingtonePickerScreen
import com.example.alarmscratch.settings.AlarmDefaultsScreen
import kotlin.reflect.typeOf

@Composable
fun AlarmApp() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = CoreScreen.route
    ) {
        // Core Screen
        composable(route = CoreScreen.route) {
            CoreScreen(rootNavHostController = navHostController)
        }

        // Alarm Creation Screen
        composable<AlarmCreationScreen>(
            typeMap = mapOf(typeOf<CoreNavComponent2?>() to DestinationNavType.CoreNavComponent2Type)
        ) {
            AlarmCreationScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUriString ->
                    navHostController.navigate(RingtonePickerScreen(ringtoneUriString = ringtoneUriString)) { launchSingleTop = true }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Edit Screen
        composable<AlarmEditScreen>(
            typeMap = mapOf(typeOf<CoreNavComponent2?>() to DestinationNavType.CoreNavComponent2Type)
        ) {
            AlarmEditScreen(
                navHostController = navHostController,
                navigateToRingtonePickerScreen = { ringtoneUriString ->
                    navHostController.navigate(RingtonePickerScreen(ringtoneUriString = ringtoneUriString)) { launchSingleTop = true }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Ringtone Picker Screen
        composable<RingtonePickerScreen>(
            typeMap = mapOf(typeOf<CoreNavComponent2?>() to DestinationNavType.CoreNavComponent2Type)
        ) {
            RingtonePickerScreen(
                navHostController = navHostController,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Alarm Defaults Screen
        composable(route = AlarmDefaultsScreen.route) {
            AlarmDefaultsScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

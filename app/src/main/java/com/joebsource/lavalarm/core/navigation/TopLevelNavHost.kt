package com.joebsource.lavalarm.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joebsource.lavalarm.alarm.ui.alarmcreate.AlarmCreationScreen
import com.joebsource.lavalarm.alarm.ui.alarmedit.AlarmEditScreen
import com.joebsource.lavalarm.core.extension.navigateSingleTop
import com.joebsource.lavalarm.core.ui.core.CoreScreen
import com.joebsource.lavalarm.core.ui.ringtonepicker.RingtonePickerScreen
import com.joebsource.lavalarm.settings.ui.alarmdefaults.AlarmDefaultsScreen
import com.joebsource.lavalarm.settings.ui.generalsettings.GeneralSettingsScreen

@Composable
fun TopLevelNavHost() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = Destination.CoreScreen,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 400))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 400))
        }
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

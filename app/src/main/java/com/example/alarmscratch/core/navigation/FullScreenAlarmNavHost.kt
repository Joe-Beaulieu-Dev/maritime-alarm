package com.example.alarmscratch.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmScreen
import com.example.alarmscratch.alarm.ui.fullscreenalert.FullScreenAlarmViewModel
import com.example.alarmscratch.alarm.ui.fullscreenalert.PostAlarmConfirmationScreen

@Composable
fun FullScreenAlarmNavHost(
    navHostController: NavHostController,
    fullScreenAlarmViewModel: FullScreenAlarmViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.FullScreenAlarmScreen
    ) {
        // Full Screen Alarm Screen
        composable<Destination.FullScreenAlarmScreen> {
            FullScreenAlarmScreen(fullScreenAlarmViewModel = fullScreenAlarmViewModel)
        }

        // Post Alarm Confirmation Screen
        composable<Destination.PostAlarmConfirmationScreen> {
            val route = it.toRoute<Destination.PostAlarmConfirmationScreen>()
            val fullScreenAlarmButton = route.fullScreenAlarmButton
            val snoozeDuration = route.snoozeDuration

            PostAlarmConfirmationScreen(
                fullScreenAlarmButton = fullScreenAlarmButton,
                snoozeDuration = snoozeDuration
            )
        }
    }
}

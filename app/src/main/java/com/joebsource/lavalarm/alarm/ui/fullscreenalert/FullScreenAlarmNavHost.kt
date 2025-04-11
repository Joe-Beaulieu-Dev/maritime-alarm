package com.joebsource.lavalarm.alarm.ui.fullscreenalert

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.joebsource.lavalarm.core.navigation.Destination

@Composable
fun FullScreenAlarmNavHost(
    navHostController: NavHostController,
    fullScreenAlarmViewModel: FullScreenAlarmViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.FullScreenAlarmScreen,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 300))
        }
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

package com.example.alarmscratch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alarmscratch.ui.alarmlist.composable.AlarmListScreen

@Composable
fun AlarmNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.AlarmList.route,
        modifier = modifier
    ) {
        composable(route = Destination.AlarmList.route) {
            AlarmListScreen()
        }
    }
}

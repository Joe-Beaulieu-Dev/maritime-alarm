package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.ui.navigation.AlarmNavHost

@Composable
fun AlarmApp(modifier: Modifier = Modifier) {
    AlarmNavHost(
        navController = rememberNavController(),
        modifier = modifier
    )
}

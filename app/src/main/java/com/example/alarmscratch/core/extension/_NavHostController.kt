package com.example.alarmscratch.core.extension

import androidx.navigation.NavHostController
import com.example.alarmscratch.core.navigation.Destination

fun NavHostController.navigateSingleTop(destination: Destination) {
    navigate(destination) { launchSingleTop = true }
}

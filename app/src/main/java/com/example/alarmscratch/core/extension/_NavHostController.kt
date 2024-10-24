package com.example.alarmscratch.core.extension

import androidx.navigation.NavHostController
import com.example.alarmscratch.core.navigation.Destination

fun NavHostController.navigateSingleTop(destination: Destination) {
    navigate(destination) { launchSingleTop = true }
}

fun NavHostController.getStringFromBackStack(key: String): String? =
    try {
        currentBackStackEntry?.savedStateHandle?.get(key)
    } catch (e: Exception) {
        null
    }

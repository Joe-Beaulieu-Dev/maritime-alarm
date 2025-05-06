package com.octrobi.lavalarm.core.extension

import androidx.navigation.NavHostController
import com.octrobi.lavalarm.core.navigation.Destination

fun NavHostController.navigateSingleTop(destination: Destination) {
    navigate(destination) { launchSingleTop = true }
}

fun NavHostController.getStringFromBackStack(key: String): String? =
    try {
        currentBackStackEntry?.savedStateHandle?.get(key)
    } catch (e: Exception) {
        null
    }

fun NavHostController.getAndRemoveStringFromBackStack(key: String): String? =
    try {
        // remove() returns the previous value
        currentBackStackEntry?.savedStateHandle?.remove(key)
    } catch (e: Exception) {
        null
    }

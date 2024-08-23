package com.example.alarmscratch.core.extension

import androidx.navigation.NavHostController

fun NavHostController.navigateSingleTop(route: String) =
    navigate(route) {
        launchSingleTop = true
    }

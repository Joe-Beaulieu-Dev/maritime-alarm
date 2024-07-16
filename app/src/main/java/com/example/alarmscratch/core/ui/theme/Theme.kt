package com.example.alarmscratch.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Comments below are for what these colors would change by default.
// Some of these Composables' attributes have been modified in the code,
// therefore, some of the Composables listed below might have different
// colors.
//
// These comments are just to tell you what would change if each
// Composable was using its default color settings.
private val NauticalColorScheme = darkColorScheme(
    /*
     * Switch -> checkedTrackColor
     */
    primary = BoatHull,
    /*
     * Switch -> checkedThumbColor
     */
    onPrimary = BoatSails,
    /*
     * FloatingActionButton -> containerColor
     */
    primaryContainer = BrightLavaOrange,
    /*
     * FloatingActionButton -> contentColor
     */
    onPrimaryContainer = MaxBrightLavaOrange,
    /*
     * NavigationBarItem -> indicatorColor
     */
    secondaryContainer = DarkLavaRed,
    /*
     * NavigationBarItem -> selectedIconColor
     */
    onSecondaryContainer = MaxBrightLavaOrange,
    /*
     * Surface -> color
     * NavigationBar -> containerColor
     */
    surface = DarkVolcanicRock,
    /*
     * NavigationBarItem -> selectedTextColor
     * DropdownMenuItem -> textColor
     */
    onSurface = MaxBrightLavaOrange,
    /*
     * Card -> containerColor, modified disabledContainerColor
     * Switch -> uncheckedTrackColor
     * DropdownMenuItem -> leadingIconColor
     */
    surfaceVariant = VolcanicRock,
    /*
     * Card -> contentColor (Text and Icon)
     * NavigationBarItem -> unselectedIconColor, unselectedTextColor
     */
    onSurfaceVariant = MaxBrightLavaOrange,
    /*
     * Switch -> uncheckedThumbColor, uncheckedBoarderColor
     */
    outline = LightVolcanicRock
)

@Composable
fun AlarmScratchTheme(content: @Composable () -> Unit) {
    // Change status bar colors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SkyBlue.toArgb()
            // color scheme is always the same, so make the StatusBar text and icons always white
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // Set theme
    MaterialTheme(
        colorScheme = NauticalColorScheme,
        typography = Typography,
        content = content
    )
}
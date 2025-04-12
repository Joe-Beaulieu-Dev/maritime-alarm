package com.joebsource.lavalarm.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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
fun LavalarmTheme(content: @Composable () -> Unit) {
    // Set theme
    MaterialTheme(
        colorScheme = NauticalColorScheme,
        typography = Typography,
        content = content
    )
}

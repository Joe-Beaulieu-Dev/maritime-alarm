package com.example.alarmscratch.core.ui.theme

import androidx.compose.ui.graphics.Color

// SkylineHeader
val InCloudBlack = Color(0xFF404040)// 25% Lighter Black
val SkyBlue = Color(0xFFc2e0ff) // 80% Lighter TopOceanBlue

// Sail Boat
val BoatHull = Color(0xFFbe1931)
val BoatSails = Color(0xFFf9f6e2)
val DarkerBoatSails = Color(0xFFf3edc6) // 7% Darker BoatSails
val WayDarkerBoatSails = Color(0xFF625814) // 75% Darker BoatSails

// Ocean Background
val TopOceanBlue = Color(0xFF0066CC)
val BottomOceanBlue = Color(0xFF00264a)

// BeachBackdrop
val BeachOcean = Color(0xFF66b2ff)
val WetSand = Color(0xFFffe3a0)
val DrySand = Color(0xFFfff5be)
val TransparentBlack = Color(0x10000000)

// Volcanic Rock
val LightVolcanicRock = Color(0xFF6a6a68) // 25% Lighter VolcanicRock
val VolcanicRock = Color(0xFF373736)
val MediumVolcanicRock = Color(0xFF2f2f2e)
val DarkVolcanicRock = Color(0xFF232322) // 37% Darker VolcanicRock

// Lava
val MaxBrightLavaOrange = Color(0xFFfefee8) // 90% Lighter #f2f217
val AncientLavaOrange = Color(0xFFff6600)
val BrightLavaOrange = Color(0xFFe87931)
val BrightLavaRed = Color(0xFFcf1020)
val MediumLavaRed = Color(0xFFa60d1a) // 20% Darker BrightLavaRed
val DarkLavaRed = Color(0xFF660f00) // 60% Darker #ff2500

// General
val SelectedGreen = Color(0xFF00b300)

// Navigation
val NavIconActive = BrightLavaRed
val NavTextActive = BrightLavaRed
val NavIconInactive = LightVolcanicRock
val NavTextInactive = LightVolcanicRock
val NavIndicator = VolcanicRock

// OS Default Clones
// Copied from ColorScheme.DisabledAlpha
const val AndroidDisabledAlpha = 0.38f
// Copied from EdgeToEdge.DefaultDarkScrim. Alpha value modified from 0x80.
val AndroidDefaultDarkScrim = Color(red = 0x1b, green = 0x1b, blue = 0x1b, alpha = 0xFF)

package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BeachOcean
import com.example.alarmscratch.core.ui.theme.DrySand
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.core.ui.theme.WetSand

@Composable
fun BeachBackdrop(modifier: Modifier = Modifier) {
    // Heights
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp
    val waterHeight = screenHeight / 2
    val skylineHeight = screenHeight / 4

    // Offsets
    val wetSandOffset = screenHeight * 0.13f
    val seaFoamOffset = screenHeight * 0.065f
    val waterOffset = screenHeight * 0.05f

    Box(modifier = modifier.fillMaxSize()) {
        // Dry and Wet Sand
        // Dry Sand
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = DrySand)
        )
        // Wet Sand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = wetSandOffset)
                .height(waterHeight)
                .clip(WetSandLineShape())
                .background(color = WetSand)
        )

        // Sea Foam, Water, and Skyline
        // Sea Foam
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = seaFoamOffset)
                .height(waterHeight)
                .clip(WaveShape())
                .background(color = Color.White)
        )
        // Water
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = waterOffset)
                .height(waterHeight)
                .clip(WaveShape())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            TopOceanBlue,
                            BeachOcean
                        )
                    )
                )
        )
        // Skyline
        BeachSkyline(skylineHeight = skylineHeight)
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun BeachBackdropPreview() {
    AlarmScratchTheme {
        BeachBackdrop()
    }
}

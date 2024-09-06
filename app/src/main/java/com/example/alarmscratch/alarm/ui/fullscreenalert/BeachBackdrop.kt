package com.example.alarmscratch.alarm.ui.fullscreenalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.core.component.SailBoat
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BeachOcean
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DrySand
import com.example.alarmscratch.core.ui.theme.SkyBlue
import com.example.alarmscratch.core.ui.theme.TopOceanBlue
import com.example.alarmscratch.core.ui.theme.WetSand

@Composable
fun BeachBackdrop() {
    val config = LocalConfiguration.current
    val configHeight = config.screenHeightDp.dp

    val wetSandOffset = configHeight * 0.15f
    val seaFoamOffset = configHeight * 0.065f
    val waterOffset = configHeight * 0.05f
    val skyHeight = configHeight / 4
    val waterHeight = configHeight / 2

    Box(modifier = Modifier.fillMaxSize()) {

        // Dry and Wet Sand
        // Dry sand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = DrySand)
        )
        // Wet sand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = wetSandOffset)
                .height(waterHeight)
                .clip(WaveShape())
                .background(color = WetSand)
        )

        // Foam, Water, and Skyline
        // Foam
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
        // Sky
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(skyHeight)
                .background(color = SkyBlue)
        )
        // Sail Boat
        SailBoat(
            boatSize = 30.dp,
            hullColor = BoatHull,
            sailColor = BoatSails,
            modifier = Modifier.offset(x = 45.dp, y = skyHeight - 29.dp)
        )
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

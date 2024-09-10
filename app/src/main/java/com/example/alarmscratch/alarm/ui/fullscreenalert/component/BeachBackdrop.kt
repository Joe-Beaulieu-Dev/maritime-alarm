package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    // Heights
    val config = LocalConfiguration.current
    val configHeight = config.screenHeightDp.dp
    val waterHeight = configHeight / 2
    val skyHeight = configHeight / 4

    // Offsets
    val wetSandOffset = configHeight * 0.15f
    val seaFoamOffset = configHeight * 0.065f
    val waterOffset = configHeight * 0.05f

    Box(modifier = Modifier.fillMaxSize()) {
        // Dry and Wet Sand
        // Dry Sand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = DrySand)
        )
        // Wet Sand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = wetSandOffset)
                .height(waterHeight)
                .clip(WaveShape())
                .background(color = WetSand)
        )

        // Sea Foam, Water, Skyline, and Boat
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
        BeachSkyline(skyHeight = skyHeight)

        // Boat
        SailBoat(
            boatSize = 30.dp,
            hullColor = BoatHull,
            sailColor = BoatSails,
            modifier = Modifier.offset(x = 45.dp, y = skyHeight - 29.dp)
        )
    }
}

@Composable
fun BeachSkyline(
    skyHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(skyHeight)
            .background(color = SkyBlue)
    ) {
        /*
         ***********
         ** Row 1 **
         ***********
         */
        // First Cloud
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(50.dp)
                .height(30.dp)
                .offset(x = 10.dp, y = 35.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Second Cloud
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(30.dp)
                .height(20.dp)
                .offset(x = (-110).dp, y = 5.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Third Cloud Small Part
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(75.dp)
                .height(30.dp)
                .offset(x = (-40).dp, y = 30.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )
        // Third Cloud Large Part
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(110.dp)
                .height(50.dp)
                .offset(x = 0.dp, y = 5.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Sun
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(50.dp)
                .height(50.dp)
                .offset(x = (-20).dp, y = 0.dp)
                .clip(shape = CircleShape)
                .background(color = Color.Yellow)
        )
        // Sun Cloud
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(90.dp)
                .height(30.dp)
                .offset(x = (-10).dp, y = 20.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        /*
         ***********
         ** Row 2 **
         ***********
         */
        // First Cloud Small Part
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(35.dp)
                .height(25.dp)
                .offset(x = 10.dp, y = 108.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )
        // First Cloud Large Part
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(80.dp)
                .height(40.dp)
                .offset(x = 15.dp, y = 100.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Second Cloud Small Part
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(60.dp)
                .height(30.dp)
                .offset(x = (-50).dp, y = 82.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )
        // Second Cloud Large Part
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(75.dp)
                .height(40.dp)
                .offset(x = (-30).dp, y = 85.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Third Cloud
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(30.dp)
                .height(20.dp)
                .offset(x = 55.dp, y = 70.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )

        // Fourth Cloud Small Part
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(50.dp)
                .height(25.dp)
                .offset(x = (-45).dp, y = 98.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
        )
        // Fourth Cloud Large Part
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(70.dp)
                .height(35.dp)
                .offset(x = (-10).dp, y = 85.dp)
                .clip(shape = CircleShape)
                .background(color = Color.White)
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

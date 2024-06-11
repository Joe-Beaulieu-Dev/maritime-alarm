package com.example.alarmscratch.ui.alarmlist.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.ui.theme.NavIconActive
import com.example.alarmscratch.ui.theme.NavIconInactive
import com.example.alarmscratch.ui.theme.NavIndicator
import com.example.alarmscratch.ui.theme.NavTextActive
import com.example.alarmscratch.ui.theme.NavTextInactive
import com.example.alarmscratch.ui.theme.OtherLavaRed

@Composable
fun VolcanoNavigationBar(
    modifier: Modifier = Modifier,
    selectedDestination: Int = 0,
    onDestinationChange: (Int) -> Unit
) {
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavIconActive,
        selectedTextColor = NavTextActive,
        unselectedIconColor = NavIconInactive,
        unselectedTextColor = NavTextInactive,
        indicatorColor = NavIndicator
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        VolcanoWithLava()

        NavigationBar(
            tonalElevation = 0.dp
        ) {
            // Alarm
            NavigationBarItem(
                selected = selectedDestination == 0,
                onClick = { onDestinationChange(0) },
                icon = {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = null)
                },
                label = {
                    Text(text = stringResource(id = R.string.nav_alarm))
                },
                colors = navColors
            )

            // Settings
            NavigationBarItem(
                selected = selectedDestination == 1,
                onClick = { onDestinationChange(1) },
                icon = {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                },
                label = {
                    Text(text = stringResource(id = R.string.nav_settings))
                },
                colors = navColors
            )
        }
    }
}

@Composable
fun VolcanoWithLava(modifier: Modifier = Modifier) {
    val lavaColor = OtherLavaRed

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .width(144.dp)
            .height(58.dp)
    ) {
        Volcano(modifier = Modifier.align(Alignment.BottomCenter))

        // Left Rock
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(34.dp)
                .height(20.dp)
                .offset(x = (-56).dp, y = 10.dp)
                .rotate(degrees = -28f)
                .clip(CircleShape)
                .background(DarkVolcanicRock)
        )

        // Right Rock
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(34.dp)
                .height(20.dp)
                .offset(x = 56.dp, y = 10.dp)
                .rotate(degrees = 28f)
                .clip(CircleShape)
                .background(DarkVolcanicRock)
        )

        // Top Lava
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(64.dp)
                .height(12.dp)
                .offset(x = 0.dp, y = 8.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Top Lava Bubble Left
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(22.dp)
                .height(20.dp)
                .offset(x = (-18).dp, y = 6.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Top Lava Bubble Center
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(22.dp)
                .height(20.dp)
                .offset(x = 4.dp, y = 6.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Side Lava
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = (-32).dp, y = 7.dp)
                .rotate(degrees = 32f)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Lava Drip
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = (-24).dp, y = 10.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Lava Blob
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .offset(x = (-12).dp, y = 10.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Middle Lava
        Box(
            modifier = Modifier
                .height(40.dp)
                .width(12.dp)
                .offset(x = 0.dp, y = 10.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Lava Blob
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(20.dp)
                .offset(x = 12.dp, y = 10.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Lava Drip
        Box(
            modifier = Modifier
                .height(34.dp)
                .width(12.dp)
                .offset(x = 24.dp, y = 10.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Side Lava
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = 32.dp, y = 7.dp)
                .rotate(degrees = -32f)
                .clip(CircleShape)
                .background(lavaColor)
        )
    }
}

@Composable
fun Volcano(modifier: Modifier = Modifier) {
    // Volcano coordinates
    val volcanoTopHeightY = with(LocalDensity.current) { 48.dp.toPx() }
    val volcanoTopLeftX = with(LocalDensity.current) { 30.dp.toPx() }
    val volcanoTopRightX = with(LocalDensity.current) { 90.dp.toPx() }

    // Side Volcano Left Triangle stats
    //
    // Right Side -> 48.dp high
    // Bottom Side -> 30.dp wide
    // Hypotenuse -> ~56.6.dp long
    //
    // BottomRight Corner -> 90 degrees
    // Top Angle -> ~32 degrees
    // Left Angel -> ~58 degrees

    Box(
        modifier = modifier
            .width(120.dp)
            .height(48.dp)
            .drawBehind {
                val volcano = Path().apply {
                    moveTo(x = 0f, y = size.height)
                    lineTo(x = volcanoTopLeftX, y = size.height - volcanoTopHeightY)
                    lineTo(x = volcanoTopRightX, y = size.height - volcanoTopHeightY)
                    lineTo(x = size.width, y = size.height)
                    close()
                }
                drawPath(path = volcano, color = DarkVolcanicRock)
            }
    )
}

/*
 * Preview
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF001A33
)
@Composable
private fun VolcanoNavigationBarPreview() {
    AlarmScratchTheme {
        VolcanoNavigationBar(
            modifier = Modifier.padding(top = 12.dp),
            onDestinationChange = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun VolcanoWithLavaPreview() {
    AlarmScratchTheme {
        VolcanoWithLava(
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun VolcanoPreview() {
    AlarmScratchTheme {
        Volcano(modifier = Modifier.padding(top = 20.dp))
    }
}

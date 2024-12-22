package com.example.alarmscratch.core.ui.core.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.navigation.NavComponent
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BrightLavaRed
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.MediumLavaRed
import com.example.alarmscratch.core.ui.theme.NavIconActive
import com.example.alarmscratch.core.ui.theme.NavIconInactive
import com.example.alarmscratch.core.ui.theme.NavIndicator
import com.example.alarmscratch.core.ui.theme.NavTextActive
import com.example.alarmscratch.core.ui.theme.NavTextInactive

@Composable
fun VolcanoNavigationBar(
    selectedNavComponentDest: Destination,
    onDestinationChange: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavIconActive,
        selectedTextColor = NavTextActive,
        unselectedIconColor = NavIconInactive,
        unselectedTextColor = NavTextInactive,
        indicatorColor = NavIndicator
    )

    NavigationBar(
        containerColor = DarkVolcanicRock,
        modifier = modifier
    ) {
        NavComponent.entries.forEach { navComponent ->
            NavigationBarItem(
                selected = selectedNavComponentDest == navComponent.destination,
                onClick = { onDestinationChange(navComponent.destination) },
                icon = { Icon(imageVector = navComponent.navIcon, contentDescription = null) },
                label = { Text(text = stringResource(id = navComponent.navNameRes)) },
                colors = navColors
            )
        }
    }
}

@Composable
fun VolcanoWithLava(modifier: Modifier = Modifier) {
    // Volcano Height = 48.dp
    // Lava Peek Height = 4.dp
    // Box Height = Volcano Height + Lava Peek Height
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .width(144.dp)
            .height(52.dp)
    ) {
        // Volcano
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

        // Lava
        Lava()
    }
}

@Composable
fun Lava(modifier: Modifier = Modifier) {
    // Lava Color Animation
    val lavaColorTransition = rememberInfiniteTransition(label = "lava_color_transition")
    val lavaColor by lavaColorTransition.animateColor(
        initialValue = BrightLavaRed,
        targetValue = MediumLavaRed,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                BrightLavaRed at 3000
                MediumLavaRed at 3900
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "lava_color"
    )
    val tallestLavaHeight = 40.dp
    val tallestLavaOffsetY = 4.dp

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.height(tallestLavaHeight + tallestLavaOffsetY)
    ) {
        // Top Lava
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(12.dp)
                .offset(x = 0.dp, y = 2.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Top Lava Bubble Left
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(20.dp)
                .offset(x = (-18).dp, y = 0.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Top Lava Bubble Center
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(20.dp)
                .offset(x = 4.dp, y = 0.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Side Lava
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = (-32).dp, y = 1.dp)
                .rotate(degrees = 32f)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Lava Drip
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = (-24).dp, y = 4.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Left Lava Blob
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .offset(x = (-12).dp, y = 4.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Middle Lava
        Box(
            modifier = Modifier
                .height(tallestLavaHeight)
                .width(12.dp)
                .offset(x = 0.dp, y = tallestLavaOffsetY)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Lava Blob
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(20.dp)
                .offset(x = 12.dp, y = 4.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Lava Drip
        Box(
            modifier = Modifier
                .height(34.dp)
                .width(12.dp)
                .offset(x = 24.dp, y = 4.dp)
                .clip(CircleShape)
                .background(lavaColor)
        )

        // Right Side Lava
        Box(
            modifier = Modifier
                .height(28.dp)
                .width(12.dp)
                .offset(x = 32.dp, y = 1.dp)
                .rotate(degrees = -32f)
                .clip(CircleShape)
                .background(lavaColor)
        )
    }
}

@Composable
fun Volcano(modifier: Modifier = Modifier) {
    /*
     * Volcano Left-Side Triangle stats
     *
     * Right Side -> 48.dp high
     * Bottom Side -> 30.dp wide
     * Hypotenuse -> ~56.6.dp long
     *
     * BottomRight Corner -> 90 degrees
     * Top Angle -> ~32 degrees
     * Left Angle -> ~58 degrees
     */

    // Volcano coordinates
    val density = LocalDensity.current
    val volcanoTopHeightY = with(density) { 48.dp.toPx() }
    val volcanoTopLeftX = with(density) { 30.dp.toPx() }
    val volcanoTopRightX = with(density) { 90.dp.toPx() }

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
            selectedNavComponentDest = Destination.AlarmListScreen,
            onDestinationChange = {},
            modifier = Modifier.padding(top = 20.dp)
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
        VolcanoWithLava(modifier = Modifier.padding(top = 20.dp))
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0066CC
)
@Composable
private fun LavaPreview() {
    AlarmScratchTheme {
        Lava(modifier = Modifier.padding(20.dp))
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

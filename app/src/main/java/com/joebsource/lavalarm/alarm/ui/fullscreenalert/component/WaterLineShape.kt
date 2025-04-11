package com.joebsource.lavalarm.alarm.ui.fullscreenalert.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.joebsource.lavalarm.core.ui.theme.AlarmScratchTheme
import com.joebsource.lavalarm.core.ui.theme.BeachOcean
import com.joebsource.lavalarm.core.ui.theme.WetSand

class WaterLineShape : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val waveLine = Path().apply {
            val width = size.width
            val height = size.height
            val startHeight = height * 0.9f

            // Line down the left
            lineTo(x = 0f, y = startHeight)

            // First curve
            val bezierTip1X = width * 0.25f
            val bezierTip1Y = startHeight * 1.2f
            val endpoint1X = width * 0.5f
            val endpoint1Y = startHeight
            quadraticTo(
                // Bezier tip
                x1 = bezierTip1X,
                y1 = bezierTip1Y,
                // End point
                x2 = endpoint1X,
                y2 = endpoint1Y
            )

            // Second curve
            val bezierTip2X = ((width * 0.75f - endpoint1X) / 2) + endpoint1X
            val bezierTip2Y = startHeight * 0.9f
            val endpoint2X = width * 0.75f
            val endpoint2Y = startHeight
            quadraticTo(
                // Bezier tip
                x1 = bezierTip2X,
                y1 = bezierTip2Y,
                // End point
                x2 = endpoint2X,
                y2 = endpoint2Y
            )

            // Third curve
            val bezierTip3X = ((width - endpoint2X) / 2) + endpoint2X
            val bezierTip3Y = bezierTip1Y * 0.9f
            val endpoint3X = width
            val endpoint3Y = startHeight
            quadraticTo(
                // Bezier tip
                x1 = bezierTip3X,
                y1 = bezierTip3Y,
                // End point
                x2 = endpoint3X,
                y2 = endpoint3Y
            )

            // Line down the right
            lineTo(x = width, y = 0f)

            // Line across the top
            close()
        }

        return Outline.Generic(waveLine)
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun WaterLineShapePreview() {
    AlarmScratchTheme {
        val config = LocalConfiguration.current
        val screenHeight = config.screenHeightDp.dp
        val waterHeight = screenHeight / 2

        // Wet Sand background with WaterLineShape-clipped Water on top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.75f)
                .background(color = WetSand)
        ) {
            // Water
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(waterHeight)
                    .clip(WaterLineShape())
                    .background(color = BeachOcean)
            )

            // Divider to show the actual bottom of the clipped Water Box
            HorizontalDivider(thickness = 2.dp, color = Color.Black)
        }
    }
}

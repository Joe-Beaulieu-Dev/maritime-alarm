package com.example.alarmscratch.alarm.ui.fullscreenalert.component

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
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.ui.theme.WetSand

class WetSandLineShape : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val wetSandLine = Path().apply {
            val width = size.width
            val height = size.height
            val startHeight = height * 0.95f

            // Line down the left
            lineTo(x = 0f, y = startHeight)

            // First curve
            val bezierTip1X = width * 0.25f
            val bezierTip1Y = startHeight * 1.1f
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
            val bezierTip2Y = startHeight * 0.95f
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
            val bezierTip3Y = bezierTip1Y * 0.95f
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

        return Outline.Generic(wetSandLine)
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun WetSandLineShapePreview() {
    AlarmScratchTheme {
        val config = LocalConfiguration.current
        val screenHeight = config.screenHeightDp.dp
        val wetSandHeight = screenHeight / 2

        // VolcanicRock background with WetSandLineShape-clipped Wet Sand on top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.75f)
                .background(color = VolcanicRock)
        ) {
            // Wet Sand
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(wetSandHeight)
                    .clip(WetSandLineShape())
                    .background(color = WetSand)
            )

            // Divider to show the actual bottom of the clipped Wet Sand Box
            HorizontalDivider(thickness = 2.dp, color = Color.Red)
        }
    }
}

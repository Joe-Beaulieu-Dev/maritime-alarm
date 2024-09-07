package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WaveShape : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val waveLine = Path().apply {
            val width = size.width
            val height = size.height
            val startHeight = height * 0.9f

            lineTo(x = 0f, y = startHeight)

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

            lineTo(x = width, y = 0f)
            close()
        }

        return Outline.Generic(waveLine)
    }
}

package com.joebsource.lavalarm.core.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joebsource.lavalarm.core.ui.theme.AlarmScratchTheme
import com.joebsource.lavalarm.core.ui.theme.BoatHull
import com.joebsource.lavalarm.core.ui.theme.BoatSails

@Composable
fun SailBoat(
    boatSize: Dp,
    hullColor: Color,
    sailColor: Color,
    modifier: Modifier = Modifier
) {
    // Hull coordinates
    val hullTop = with(LocalDensity.current) { (boatSize.times(other = 0.333f)).toPx() }
    val hullBottomLeft = with(LocalDensity.current) { (boatSize.times(other = 0.25f)).toPx() }
    val hullBottomRight = with(LocalDensity.current) { (boatSize.times(other = 0.75f)).toPx() }
    // Left Sail coordinate
    val leftSailCenter = with(LocalDensity.current) { (boatSize.times(other = 0.475f)).toPx() }
    // Right Sail coordinates
    val rightSailCenter = with(LocalDensity.current) { (boatSize.times(other = 0.525f)).toPx() }
    val rightSailTop = with(LocalDensity.current) { (boatSize.times(other = 0.875f)).toPx() }
    val rightSailRight = with(LocalDensity.current) { (boatSize.times(other = 0.125f)).toPx() }

    Box(
        modifier = modifier
            .size(boatSize)
            .drawBehind {
                val hull = Path().apply {
                    // Hull TopLeft
                    moveTo(x = 0f, y = size.height - hullTop)
                    // Hull TopRight
                    lineTo(x = size.width, y = size.height - hullTop)
                    // Hull BottomRight
                    lineTo(x = hullBottomRight, y = size.height)
                    // Hull BottomLeft
                    lineTo(x = hullBottomLeft, y = size.height)
                    close()
                }
                drawPath(path = hull, color = hullColor)

                val leftSail = Path().apply {
                    // Left Sail TopCenter
                    moveTo(x = leftSailCenter, y = 0f)
                    // Left Sail BottomCenter
                    lineTo(x = leftSailCenter, y = size.height - hullTop)
                    // Left Sail BottomLeft
                    lineTo(x = 0f, y = size.height - hullTop)
                    close()
                }
                drawPath(path = leftSail, color = sailColor)

                val rightSail = Path().apply {
                    // Right Sail TopCenter
                    moveTo(x = rightSailCenter, y = size.height - rightSailTop)
                    // Right Sail BottomCenter
                    lineTo(x = rightSailCenter, y = size.height - hullTop)
                    // Right Sail BottomRight
                    lineTo(x = size.width - rightSailRight, y = size.height - hullTop)
                    close()
                }
                drawPath(path = rightSail, color = sailColor)
            }
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF404040
)
@Composable
private fun SailBoatPreview() {
    AlarmScratchTheme {
        SailBoat(
            boatSize = 200.dp,
            hullColor = BoatHull,
            sailColor = BoatSails
        )
    }
}

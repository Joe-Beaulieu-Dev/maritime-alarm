package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkLavaRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Starfish(
    starfishSize: Dp,
    modifier: Modifier = Modifier
) {
    val radiusPixels = with(LocalDensity.current) { (starfishSize / 2).toPx() }

    // Outer Points
    val rightArmOuterPoint = calculateOuterPoint(radiusPixels, 0, radiusPixels)
    val headOuterPoint = calculateOuterPoint(radiusPixels, 1, radiusPixels)
    val leftArmOuterPoint = calculateOuterPoint(radiusPixels, 2, radiusPixels)
    val leftLegOuterPoint = calculateOuterPoint(radiusPixels, 3, radiusPixels)
    val rightLegOuterPoint = calculateOuterPoint(radiusPixels, 4, radiusPixels)

    // Inner Points
    val rightNeckInnerPoint = calculateInnerPoint(radiusPixels / 2.5f, 0, radiusPixels)
    val leftNeckInnerPoint = calculateInnerPoint(radiusPixels / 2.5f, 1, radiusPixels)
    val leftArmpitInnerPoint = calculateInnerPoint(radiusPixels / 2.5f, 2, radiusPixels)
    val bottomInnerPoint = calculateInnerPoint(radiusPixels / 2.5f, 3, radiusPixels)
    val rightArmpitInnerPoint = calculateInnerPoint(radiusPixels / 2.5f, 4, radiusPixels)

    Box(
        modifier = modifier
            .size(starfishSize)
            .rotate(180f)
            .drawBehind {
                val starfish = Path().apply {
                    // Top Half
                    moveTo(rightArmOuterPoint.first, rightArmOuterPoint.second)
                    lineTo(rightNeckInnerPoint.first, rightNeckInnerPoint.second)
                    lineTo(headOuterPoint.first, headOuterPoint.second)
                    lineTo(leftNeckInnerPoint.first, leftNeckInnerPoint.second)
                    lineTo(leftArmOuterPoint.first, leftArmOuterPoint.second)

                    // Bottom Half
                    lineTo(leftArmpitInnerPoint.first, leftArmpitInnerPoint.second)
                    lineTo(leftLegOuterPoint.first, leftLegOuterPoint.second)
                    lineTo(bottomInnerPoint.first, bottomInnerPoint.second)
                    lineTo(rightLegOuterPoint.first, rightLegOuterPoint.second)
                    lineTo(rightArmpitInnerPoint.first, rightArmpitInnerPoint.second)

                    close()
                }
                drawPath(path = starfish, color = DarkLavaRed)
            }
    )
}

private fun calculateOuterPoint(radiusPixels: Float, multiplier: Int, offset: Float): Pair<Float, Float> {
    val initRadians = degreesToRadians(18f)
    val adjustedRadians = initRadians + (multiplier * (initRadians * 4))

    // Offset accounts for the origin point being the top left corner on Android
    return Pair(
        (radiusPixels * cos(adjustedRadians)) + offset,
        (radiusPixels * sin(adjustedRadians)) + offset
    )
}

private fun calculateInnerPoint(radiusPixels: Float, multiplier: Int, offset: Float): Pair<Float, Float> {
    val initRadians = degreesToRadians(54f)
    val adjustedRadians = initRadians + (multiplier * degreesToRadians(72f))

    // Offset accounts for the origin point being the top left corner on Android
    return Pair(
        (radiusPixels * cos(adjustedRadians)) + offset,
        (radiusPixels * sin(adjustedRadians)) + offset
    )
}

private fun degreesToRadians(degrees: Float): Float = (degrees * (PI / 180)).toFloat()

/*
 * Preview
 */

@Preview(
    widthDp = 300,
    heightDp = 300,
    showBackground = true,
    backgroundColor = 0xFF404040
)
@Composable
private fun StarfishPreview() {
    AlarmScratchTheme {
        Starfish(300.dp)
    }
}

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
import com.example.alarmscratch.core.ui.theme.StarfishBase
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Starfish(
    starfishSize: Dp,
    modifier: Modifier = Modifier
) {
    // General Values
    val localDensity = LocalDensity.current
    val outerCircleRadius = with(localDensity) { (starfishSize / 2).toPx() }
    val innerCircleRadius = outerCircleRadius / 2.5f
    val armBulge = with(localDensity) { (starfishSize / 10).toPx() }

    // Outer Points
    val rightArmOuterPoint = calculateOuterPoint(outerCircleRadius, 0, outerCircleRadius)
    val headOuterPoint = calculateOuterPoint(outerCircleRadius, 1, outerCircleRadius)
    val leftArmOuterPoint = calculateOuterPoint(outerCircleRadius, 2, outerCircleRadius)
    val leftLegOuterPoint = calculateOuterPoint(outerCircleRadius, 3, outerCircleRadius)
    val rightLegOuterPoint = calculateOuterPoint(outerCircleRadius, 4, outerCircleRadius)

    // Inner Points
    val rightNeckInnerPoint = calculateInnerPoint(innerCircleRadius, 0, outerCircleRadius)
    val leftNeckInnerPoint = calculateInnerPoint(innerCircleRadius, 1, outerCircleRadius)
    val leftArmpitInnerPoint = calculateInnerPoint(innerCircleRadius, 2, outerCircleRadius)
    val bottomInnerPoint = calculateInnerPoint(innerCircleRadius, 3, outerCircleRadius)
    val rightArmpitInnerPoint = calculateInnerPoint(innerCircleRadius, 4, outerCircleRadius)

    Box(
        modifier = modifier
            .size(starfishSize)
            .rotate(180f)
            .drawBehind {
                val starfishBase = Path().apply {
                    /*
                     * Top Half
                     */

                    // Start: Right Arm Outer Point
                    moveTo(rightArmOuterPoint.first, rightArmOuterPoint.second)

                    // Move: Right Arm Outer Point -> Right Neck Inner Point
                    val rightArmToRightNeckCenterPointX =
                        ((rightArmOuterPoint.first - rightNeckInnerPoint.first) / 2) + rightNeckInnerPoint.first
                    val rightArmToRightNeckCenterPointY = rightArmOuterPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = rightArmToRightNeckCenterPointX,
                        y1 = rightArmToRightNeckCenterPointY + armBulge,
                        // End point
                        x2 = rightNeckInnerPoint.first,
                        y2 = rightNeckInnerPoint.second
                    )

                    // Move: Right Neck Inner Point -> Head Outer Point
                    val rightNeckToHeadCenterPointX =
                        ((rightNeckInnerPoint.first - headOuterPoint.first) / 2) + headOuterPoint.first
                    val rightNeckToHeadCenterPointY =
                        ((headOuterPoint.second - rightNeckInnerPoint.second) / 2) + rightNeckInnerPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = rightNeckToHeadCenterPointX + armBulge,
                        y1 = rightNeckToHeadCenterPointY,
                        // End point
                        x2 = headOuterPoint.first,
                        y2 = headOuterPoint.second
                    )

                    // Move: Head Outer Point -> Left Neck Inner Point
                    val headToLeftNeckCenterPointX =
                        ((headOuterPoint.first - leftNeckInnerPoint.first) / 2) + leftNeckInnerPoint.first
                    val headToLeftNeckCenterPointY =
                        ((headOuterPoint.second - leftNeckInnerPoint.second) / 2) + leftNeckInnerPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = headToLeftNeckCenterPointX - armBulge,
                        y1 = headToLeftNeckCenterPointY,
                        // End point
                        x2 = leftNeckInnerPoint.first,
                        y2 = leftNeckInnerPoint.second
                    )

                    // Move: Left Neck Inner Point -> Left Arm Outer Point
                    val leftNeckToLeftArmCenterPointX =
                        ((leftNeckInnerPoint.first - leftArmOuterPoint.first) / 2) + leftArmOuterPoint.first
                    val leftNeckToLeftArmCenterPointY = leftNeckInnerPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = leftNeckToLeftArmCenterPointX,
                        y1 = leftNeckToLeftArmCenterPointY + armBulge,
                        // End point
                        x2 = leftArmOuterPoint.first,
                        y2 = leftArmOuterPoint.second
                    )

                    /*
                     * Bottom Half
                     */

                    // Move: Left Arm Outer Point -> Left Armpit Inner Point
                    val leftArmToLeftArmpitCenterPointX =
                        ((leftArmpitInnerPoint.first - leftArmOuterPoint.first) / 2) + leftArmOuterPoint.first
                    val leftArmToLeftArmpitCenterPointY =
                        ((leftArmOuterPoint.second - leftArmpitInnerPoint.second) / 2) + leftArmpitInnerPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = leftArmToLeftArmpitCenterPointX,
                        y1 = leftArmToLeftArmpitCenterPointY - armBulge,
                        // End point
                        x2 = leftArmpitInnerPoint.first,
                        y2 = leftArmpitInnerPoint.second
                    )

                    // Move: Left Armpit Inner Point -> Left Leg Outer Point
                    val leftArmpitToLeftLegCenterPointX =
                        ((leftArmpitInnerPoint.first - leftLegOuterPoint.first) / 2) + leftLegOuterPoint.first
                    val leftArmpitToLeftLegCenterPointY =
                        ((leftArmpitInnerPoint.second - leftLegOuterPoint.second) / 2) + leftLegOuterPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = leftArmpitToLeftLegCenterPointX - armBulge,
                        y1 = leftArmpitToLeftLegCenterPointY + armBulge,
                        // End point
                        x2 = leftLegOuterPoint.first,
                        y2 = leftLegOuterPoint.second
                    )

                    // Move: Left Leg Outer Point -> Bottom Inner Point
                    val leftLegToBottomCenterPointX =
                        ((bottomInnerPoint.first - leftLegOuterPoint.first) / 2) + leftLegOuterPoint.first
                    val leftLegToBottomCenterPointY =
                        ((bottomInnerPoint.second - leftLegOuterPoint.second) / 2) + leftLegOuterPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = leftLegToBottomCenterPointX + armBulge,
                        y1 = leftLegToBottomCenterPointY - armBulge,
                        // End point
                        x2 = bottomInnerPoint.first,
                        y2 = bottomInnerPoint.second
                    )

                    // Move: Bottom Inner Point -> Right Leg Outer Point
                    val bottomToRightLegCenterPointX =
                        ((rightLegOuterPoint.first - bottomInnerPoint.first) / 2) + bottomInnerPoint.first
                    val bottomToRightLegCenterPointY =
                        ((bottomInnerPoint.second - rightLegOuterPoint.second) / 2) + rightLegOuterPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = bottomToRightLegCenterPointX - armBulge,
                        y1 = bottomToRightLegCenterPointY - armBulge,
                        // End point
                        x2 = rightLegOuterPoint.first,
                        y2 = rightLegOuterPoint.second
                    )

                    // Move: Right Leg Outer Point -> Right Armpit Inner Point
                    val rightLegToRightArmpitCenterPointX =
                        ((rightLegOuterPoint.first - rightArmpitInnerPoint.first) / 2) + rightArmpitInnerPoint.first
                    val rightLegToRightArmpitCenterPointY =
                        ((rightArmpitInnerPoint.second - rightLegOuterPoint.second) / 2) + rightLegOuterPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = rightLegToRightArmpitCenterPointX + armBulge,
                        y1 = rightLegToRightArmpitCenterPointY + armBulge,
                        // End point
                        x2 = rightArmpitInnerPoint.first,
                        y2 = rightArmpitInnerPoint.second
                    )

                    // Move: Right Armpit Inner Point -> Right Arm Outer Point
                    val rightArmpitToRightArmCenterPointX =
                        ((rightArmOuterPoint.first - rightArmpitInnerPoint.first) / 2) + rightArmpitInnerPoint.first
                    val rightArmpitToRightArmCenterPointY =
                        ((rightArmOuterPoint.second - rightArmpitInnerPoint.second) / 2) + rightArmpitInnerPoint.second
                    quadraticTo(
                        // Bezier tip
                        x1 = rightArmpitToRightArmCenterPointX,
                        y1 = rightArmpitToRightArmCenterPointY - armBulge,
                        // End point
                        x2 = rightArmOuterPoint.first,
                        y2 = rightArmOuterPoint.second
                    )

                    // Close for good measure
                    close()
                }
                drawPath(path = starfishBase, color = StarfishBase)
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
 * Previews
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

@Preview(
    widthDp = 300,
    heightDp = 300,
    showBackground = true,
    backgroundColor = 0xFFffe3a0
)
@Composable
private fun StarfishOnWetSandPreview() {
    AlarmScratchTheme {
        Starfish(300.dp)
    }
}

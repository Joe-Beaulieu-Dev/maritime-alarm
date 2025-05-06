package com.octrobi.lavalarm.alarm.ui.fullscreenalert.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.octrobi.lavalarm.core.ui.theme.LavalarmTheme
import com.octrobi.lavalarm.core.ui.theme.StarfishBasePink
import com.octrobi.lavalarm.core.util.GraphUtil

@Composable
fun StarfishBase(
    starfishSizeDp: Dp,
    starfishCoordinates: StarfishCoordinates,
    starfishBaseColor: Color
) {
    Box(
        modifier = Modifier
            .width(starfishSizeDp)
            .height(starfishSizeDp)
            .drawBehind {
                val starfishPath = Path().apply {
                    // Without this the starfish would just look like a normal star
                    val armBulgeHeight = size.width / 10

                    /*
                     * Top Half
                     */

                    // Start: Right Arm Outer Point
                    moveTo(starfishCoordinates.rightArmOuterPoint.x, starfishCoordinates.rightArmOuterPoint.y)

                    // Move: Right Arm Outer Point -> Right Neck Inner Point
                    val rightArmToRightNeckMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.rightArmOuterPoint,
                        starfishCoordinates.rightNeckInnerPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = rightArmToRightNeckMidpoint.x,
                        y1 = rightArmToRightNeckMidpoint.y + armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.rightNeckInnerPoint.x,
                        y2 = starfishCoordinates.rightNeckInnerPoint.y
                    )

                    // Move: Right Neck Inner Point -> Head Outer Point
                    val rightNeckToHeadMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.rightNeckInnerPoint,
                        starfishCoordinates.headOuterPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = rightNeckToHeadMidpoint.x + armBulgeHeight,
                        y1 = rightNeckToHeadMidpoint.y,
                        // End point
                        x2 = starfishCoordinates.headOuterPoint.x,
                        y2 = starfishCoordinates.headOuterPoint.y
                    )

                    // Move: Head Outer Point -> Left Neck Inner Point
                    val headToLeftNeckMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.headOuterPoint,
                        starfishCoordinates.leftNeckInnerPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = headToLeftNeckMidpoint.x - armBulgeHeight,
                        y1 = headToLeftNeckMidpoint.y,
                        // End point
                        x2 = starfishCoordinates.leftNeckInnerPoint.x,
                        y2 = starfishCoordinates.leftNeckInnerPoint.y
                    )

                    // Move: Left Neck Inner Point -> Left Arm Outer Point
                    val leftNeckToLeftArmMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.leftNeckInnerPoint,
                        starfishCoordinates.leftArmOuterPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = leftNeckToLeftArmMidpoint.x,
                        y1 = leftNeckToLeftArmMidpoint.y + armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.leftArmOuterPoint.x,
                        y2 = starfishCoordinates.leftArmOuterPoint.y
                    )

                    /*
                     * Bottom Half
                     */

                    // Move: Left Arm Outer Point -> Left Armpit Inner Point
                    val leftArmToLeftArmpitMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.leftArmOuterPoint,
                        starfishCoordinates.leftArmpitInnerPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = leftArmToLeftArmpitMidpoint.x,
                        y1 = leftArmToLeftArmpitMidpoint.y - armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.leftArmpitInnerPoint.x,
                        y2 = starfishCoordinates.leftArmpitInnerPoint.y
                    )

                    // Move: Left Armpit Inner Point -> Left Leg Outer Point
                    val leftArmpitToLeftLegMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.leftArmpitInnerPoint,
                        starfishCoordinates.leftLegOuterPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = leftArmpitToLeftLegMidpoint.x - armBulgeHeight,
                        y1 = leftArmpitToLeftLegMidpoint.y + armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.leftLegOuterPoint.x,
                        y2 = starfishCoordinates.leftLegOuterPoint.y
                    )

                    // Move: Left Leg Outer Point -> Bottom Inner Point
                    val leftLegToBottomMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.leftLegOuterPoint,
                        starfishCoordinates.bottomInnerPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = leftLegToBottomMidpoint.x + armBulgeHeight,
                        y1 = leftLegToBottomMidpoint.y - armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.bottomInnerPoint.x,
                        y2 = starfishCoordinates.bottomInnerPoint.y
                    )

                    // Move: Bottom Inner Point -> Right Leg Outer Point
                    val bottomToRightLegMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.bottomInnerPoint,
                        starfishCoordinates.rightLegOuterPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = bottomToRightLegMidpoint.x - armBulgeHeight,
                        y1 = bottomToRightLegMidpoint.y - armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.rightLegOuterPoint.x,
                        y2 = starfishCoordinates.rightLegOuterPoint.y
                    )

                    // Move: Right Leg Outer Point -> Right Armpit Inner Point
                    val rightLegToRightArmpitMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.rightLegOuterPoint,
                        starfishCoordinates.rightArmpitInnerPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = rightLegToRightArmpitMidpoint.x + armBulgeHeight,
                        y1 = rightLegToRightArmpitMidpoint.y + armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.rightArmpitInnerPoint.x,
                        y2 = starfishCoordinates.rightArmpitInnerPoint.y
                    )

                    // Move: Right Armpit Inner Point -> Right Arm Outer Point
                    val rightArmpitToRightArmMidpoint = GraphUtil.findMidpoint(
                        starfishCoordinates.rightArmpitInnerPoint,
                        starfishCoordinates.rightArmOuterPoint
                    )
                    quadraticTo(
                        // Bezier tip
                        x1 = rightArmpitToRightArmMidpoint.x,
                        y1 = rightArmpitToRightArmMidpoint.y - armBulgeHeight,
                        // End point
                        x2 = starfishCoordinates.rightArmOuterPoint.x,
                        y2 = starfishCoordinates.rightArmOuterPoint.y
                    )

                    // Close for good measure
                    close()
                }
                drawPath(path = starfishPath, color = starfishBaseColor)
            }
    )
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF404040
)
@Composable
private fun StarfishBasePreview() {
    val starfishSizeDp = 300.dp
    val outerCircleRadiusPx = with(LocalDensity.current) { (starfishSizeDp / 2).toPx() }
    val starfishCoordinates = StarfishCoordinates(outerCircleRadiusPx)

    LavalarmTheme {
        StarfishBase(
            starfishSizeDp = starfishSizeDp,
            starfishCoordinates = starfishCoordinates,
            starfishBaseColor = StarfishBasePink
        )
    }
}

package com.joebsource.lavalarm.alarm.ui.fullscreenalert.component

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.StarfishBasePink
import com.joebsource.lavalarm.core.ui.theme.StarfishDotWhite
import com.joebsource.lavalarm.core.util.GraphUtil

@Composable
fun StarfishDotStar(
    starfishSizeDp: Dp,
    centeredOriginPx: PointF,
    starfishCoordinates: StarfishCoordinates,
    starfishDotColor: Color
) {
    // General
    val starfishSizePx = with(LocalDensity.current) { starfishSizeDp.toPx() }

    // Dot size
    val dotLargeRadius = starfishSizePx / 30
    val dotMediumRadius = starfishSizePx / 35
    val dotSmallRadius = starfishSizePx / 40
    val dotExtraSmallRadius = starfishSizePx / 45

    // Dot spacing
    val dotLargeGap = 0.25f
    val dotMediumGap = 0.45f
    val dotSmallGap = 0.65f
    val dotExtraSmallGap = 0.85f

    /*
     * Dot Coordinates
     */

    // Right Arm
    val rightArmDot1 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightArmOuterPoint, dotLargeGap)
    val rightArmDot2 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightArmOuterPoint, dotMediumGap)
    val rightArmDot3 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightArmOuterPoint, dotSmallGap)
    val rightArmDot4 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightArmOuterPoint, dotExtraSmallGap)
    // Head
    val headDot1 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.headOuterPoint, dotLargeGap)
    val headDot2 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.headOuterPoint, dotMediumGap)
    val headDot3 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.headOuterPoint, dotSmallGap)
    val headDot4 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.headOuterPoint, dotExtraSmallGap)
    // Left Arm
    val leftArmDot1 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftArmOuterPoint, dotLargeGap)
    val leftArmDot2 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftArmOuterPoint, dotMediumGap)
    val leftArmDot3 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftArmOuterPoint, dotSmallGap)
    val leftArmDot4 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftArmOuterPoint, dotExtraSmallGap)
    // Left Leg
    val leftLegDot1 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftLegOuterPoint, dotLargeGap)
    val leftLegDot2 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftLegOuterPoint, dotMediumGap)
    val leftLegDot3 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftLegOuterPoint, dotSmallGap)
    val leftLegDot4 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.leftLegOuterPoint, dotExtraSmallGap)
    // Right Leg
    val rightLegDot1 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightLegOuterPoint, dotLargeGap)
    val rightLegDot2 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightLegOuterPoint, dotMediumGap)
    val rightLegDot3 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightLegOuterPoint, dotSmallGap)
    val rightLegDot4 = GraphUtil.findPointAcrossLine(centeredOriginPx, starfishCoordinates.rightLegOuterPoint, dotExtraSmallGap)

    Box(
        modifier = Modifier
            .width(starfishSizeDp)
            .height(starfishSizeDp)
            .drawBehind {
                // Right Arm Dots
                drawCircle(color = starfishDotColor, radius = dotLargeRadius, center = Offset(x = rightArmDot1.x, rightArmDot1.y))
                drawCircle(color = starfishDotColor, radius = dotMediumRadius, center = Offset(x = rightArmDot2.x, rightArmDot2.y))
                drawCircle(color = starfishDotColor, radius = dotSmallRadius, center = Offset(x = rightArmDot3.x, rightArmDot3.y))
                drawCircle(color = starfishDotColor, radius = dotExtraSmallRadius, center = Offset(x = rightArmDot4.x, rightArmDot4.y))

                // Head Dots
                drawCircle(color = starfishDotColor, radius = dotLargeRadius, center = Offset(x = headDot1.x, headDot1.y))
                drawCircle(color = starfishDotColor, radius = dotMediumRadius, center = Offset(x = headDot2.x, headDot2.y))
                drawCircle(color = starfishDotColor, radius = dotSmallRadius, center = Offset(x = headDot3.x, headDot3.y))
                drawCircle(color = starfishDotColor, radius = dotExtraSmallRadius, center = Offset(x = headDot4.x, headDot4.y))

                // Left Arm Dots
                drawCircle(color = starfishDotColor, radius = dotLargeRadius, center = Offset(x = leftArmDot1.x, leftArmDot1.y))
                drawCircle(color = starfishDotColor, radius = dotMediumRadius, center = Offset(x = leftArmDot2.x, leftArmDot2.y))
                drawCircle(color = starfishDotColor, radius = dotSmallRadius, center = Offset(x = leftArmDot3.x, leftArmDot3.y))
                drawCircle(color = starfishDotColor, radius = dotExtraSmallRadius, center = Offset(x = leftArmDot4.x, leftArmDot4.y))

                // Left Leg Dots
                drawCircle(color = starfishDotColor, radius = dotLargeRadius, center = Offset(x = leftLegDot1.x, leftLegDot1.y))
                drawCircle(color = starfishDotColor, radius = dotMediumRadius, center = Offset(x = leftLegDot2.x, leftLegDot2.y))
                drawCircle(color = starfishDotColor, radius = dotSmallRadius, center = Offset(x = leftLegDot3.x, leftLegDot3.y))
                drawCircle(color = starfishDotColor, radius = dotExtraSmallRadius, center = Offset(x = leftLegDot4.x, leftLegDot4.y))

                // Right Leg Dots
                drawCircle(color = starfishDotColor, radius = dotLargeRadius, center = Offset(x = rightLegDot1.x, rightLegDot1.y))
                drawCircle(color = starfishDotColor, radius = dotMediumRadius, center = Offset(x = rightLegDot2.x, rightLegDot2.y))
                drawCircle(color = starfishDotColor, radius = dotSmallRadius, center = Offset(x = rightLegDot3.x, rightLegDot3.y))
                drawCircle(color = starfishDotColor, radius = dotExtraSmallRadius, center = Offset(x = rightLegDot4.x, rightLegDot4.y))
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
private fun StarfishDotStarPreview() {
    val starfishSizeDp = 300.dp
    val outerCircleRadiusPx = with(LocalDensity.current) { (starfishSizeDp / 2).toPx() }
    val centeredOriginPx = PointF(outerCircleRadiusPx, outerCircleRadiusPx)

    LavalarmTheme {
        StarfishDotStar(
            starfishSizeDp = starfishSizeDp,
            centeredOriginPx = centeredOriginPx,
            starfishCoordinates = StarfishCoordinates(outerCircleRadiusPx),
            starfishDotColor = StarfishDotWhite
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF404040
)
@Composable
private fun StarfishDotStarOnStarfishPreview() {
    val starfishSizeDp = 300.dp
    val outerCircleRadiusPx = with(LocalDensity.current) { (starfishSizeDp / 2).toPx() }
    val centeredOriginPx = PointF(outerCircleRadiusPx, outerCircleRadiusPx)

    LavalarmTheme {
        // Starfish Base with Dots
        Box {
            // Starfish Base
            StarfishBase(
                starfishSizeDp = starfishSizeDp,
                starfishCoordinates = StarfishCoordinates(outerCircleRadiusPx),
                starfishBaseColor = StarfishBasePink
            )

            // Starfish Dot Star
            StarfishDotStar(
                starfishSizeDp = starfishSizeDp,
                centeredOriginPx = centeredOriginPx,
                starfishCoordinates = StarfishCoordinates(outerCircleRadiusPx),
                starfishDotColor = StarfishDotWhite
            )
        }
    }
}

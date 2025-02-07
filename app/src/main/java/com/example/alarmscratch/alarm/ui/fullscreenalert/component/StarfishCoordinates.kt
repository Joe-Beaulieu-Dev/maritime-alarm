package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import android.graphics.PointF
import com.example.alarmscratch.core.util.GraphUtil
import kotlin.math.cos
import kotlin.math.sin

class StarfishCoordinates(outerCircleRadiusPx: Float) {
    // General
    private val innerCircleRadiusPx = outerCircleRadiusPx / 2.5f

    // Outer Points
    val rightArmOuterPoint = calculateOuterPoint(outerCircleRadiusPx, 0, outerCircleRadiusPx)
    val headOuterPoint = calculateOuterPoint(outerCircleRadiusPx, 1, outerCircleRadiusPx)
    val leftArmOuterPoint = calculateOuterPoint(outerCircleRadiusPx, 2, outerCircleRadiusPx)
    val leftLegOuterPoint = calculateOuterPoint(outerCircleRadiusPx, 3, outerCircleRadiusPx)
    val rightLegOuterPoint = calculateOuterPoint(outerCircleRadiusPx, 4, outerCircleRadiusPx)

    // Inner Points
    val rightNeckInnerPoint = calculateInnerPoint(innerCircleRadiusPx, 0, outerCircleRadiusPx)
    val leftNeckInnerPoint = calculateInnerPoint(innerCircleRadiusPx, 1, outerCircleRadiusPx)
    val leftArmpitInnerPoint = calculateInnerPoint(innerCircleRadiusPx, 2, outerCircleRadiusPx)
    val bottomInnerPoint = calculateInnerPoint(innerCircleRadiusPx, 3, outerCircleRadiusPx)
    val rightArmpitInnerPoint = calculateInnerPoint(innerCircleRadiusPx, 4, outerCircleRadiusPx)

    private fun calculateOuterPoint(radiusPx: Float, multiplier: Int, offset: Float): PointF {
        val initRadians = GraphUtil.degreesToRadians(18f)
        val adjustedRadians = initRadians + (multiplier * GraphUtil.degreesToRadians(72f))

        // Offset accounts for the origin point being the top left corner on Android
        return PointF(
            (radiusPx * cos(adjustedRadians)) + offset,
            (radiusPx * sin(adjustedRadians)) + offset
        )
    }

    private fun calculateInnerPoint(radiusPx: Float, multiplier: Int, offset: Float): PointF {
        val initRadians = GraphUtil.degreesToRadians(54f)
        val adjustedRadians = initRadians + (multiplier * GraphUtil.degreesToRadians(72f))

        // Offset accounts for the origin point being the top left corner on Android
        return PointF(
            (radiusPx * cos(adjustedRadians)) + offset,
            (radiusPx * sin(adjustedRadians)) + offset
        )
    }
}

package com.example.alarmscratch.alarm.ui.fullscreenalert.component

import android.graphics.PointF
import com.example.alarmscratch.core.util.GraphUtil

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

    private fun calculateOuterPoint(radiusPx: Float, stepMultiplier: Int, offsetPx: Float): PointF {
        val initRadians = GraphUtil.degreesToRadians(18f)
        val adjustedRadians = initRadians + (stepMultiplier * GraphUtil.degreesToRadians(72f))

        return GraphUtil.findPointOnCircumference(radiusPx, adjustedRadians, offsetPx)
    }

    private fun calculateInnerPoint(radiusPx: Float, stepMultiplier: Int, offsetPx: Float): PointF {
        val initRadians = GraphUtil.degreesToRadians(54f)
        val adjustedRadians = initRadians + (stepMultiplier * GraphUtil.degreesToRadians(72f))

        return GraphUtil.findPointOnCircumference(radiusPx, adjustedRadians, offsetPx)
    }
}

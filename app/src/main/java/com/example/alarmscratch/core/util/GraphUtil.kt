package com.example.alarmscratch.core.util

import android.graphics.PointF
import kotlin.math.PI

object GraphUtil {

    /*
     * Calculate Points
     */

    fun calculateMidpoint(point1: PointF, point2: PointF): PointF =
        PointF(
            ((point1.x - point2.x) / 2) + point2.x,
            ((point1.y - point2.y) / 2) + point2.y
        )

    fun calculateNewPoint(
        adjustedOrigin: PointF,
        armOuterPoint: PointF,
        percentageAcross: Float
    ): PointF =
        PointF(
            adjustedOrigin.x + ((armOuterPoint.x - adjustedOrigin.x) * percentageAcross),
            adjustedOrigin.y + ((armOuterPoint.y - adjustedOrigin.y) * percentageAcross)
        )

    /*
     * Convert
     */

    fun degreesToRadians(degrees: Float): Float = (degrees * (PI / 180)).toFloat()
}

package com.octrobi.lavalarm.core.util

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GraphUtil {

    /*
     * Find Points
     */

    /**
     * Finds the midpoint of a line segment given the 2 endpoints.
     * It doesn't matter which endpoint is passed to [endPoint1] or [endPoint2],
     * you will get the same midpoint either way.
     *
     * @param endPoint1 one endpoint of the line segment
     * @param endPoint2 the other endpoint of the line segment
     *
     * @return the midpoint of the line segment
     */
    fun findMidpoint(endPoint1: PointF, endPoint2: PointF): PointF =
        PointF(
            ((endPoint1.x - endPoint2.x) / 2) + endPoint2.x,
            ((endPoint1.y - endPoint2.y) / 2) + endPoint2.y
        )

    /**
     * Finds the point that is a given percentage across a line segment, traveling from
     * the [startPoint] endpoint towards the [finishPoint] endpoint. Because this function
     * calculates the direction of travel as [startPoint] to [finishPoint], if you swap
     * the points that you pass in then this function will return a point that is a
     * [percentageAcross] in the opposite direction.
     *
     * @param startPoint the starting endpoint of the line segment
     * @param finishPoint the finishing endpoint of the line segment
     *
     * @return the point that is the desired [percentageAcross] the line segment
     * when traveling from [startPoint] towards [finishPoint]
     */
    fun findPointAcrossLine(
        startPoint: PointF,
        finishPoint: PointF,
        percentageAcross: Float
    ): PointF =
        PointF(
            startPoint.x + ((finishPoint.x - startPoint.x) * percentageAcross),
            startPoint.y + ((finishPoint.y - startPoint.y) * percentageAcross)
        )

    /**
     * Finds a point on the circumference of a circle given the radius, radians for arc length, and an offset.
     *
     * The offset is used to modify the returned point's x and y values. These values must be
     * modified because on Android the origin point of a plane is the top left corner,
     * rather than the center. This offset modifies the coordinates so they are relative to
     * a radius that extends from a centered origin point.
     *
     * @param radiusPx the radius of the circle
     * @param radians radians used to calculate the distance across the circumference of the circle
     * @param offsetPx how much to offset the returned point's x and y values
     *
     * @return a point along the circumference of a circle
     */
    fun findPointOnCircumference(
        radiusPx: Float,
        radians: Float,
        offsetPx: Float
    ): PointF =
        PointF(
            (radiusPx * cos(radians)) + offsetPx,
            (radiusPx * sin(radians)) + offsetPx
        )

    /*
     * Convert
     */

    /**
     * Converts degrees to radians.
     *
     * @param degrees degrees to be converted to radians
     *
     * @return radians converted from [degrees]
     */
    fun degreesToRadians(degrees: Float): Float =
        (degrees * (PI / 180)).toFloat()
}

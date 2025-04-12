package com.joebsource.lavalarm.alarm.ui.fullscreenalert.component

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.StarfishBasePink
import com.joebsource.lavalarm.core.ui.theme.StarfishDotWhite

@Composable
fun Starfish(
    starfishSize: Dp,
    starfishBaseColor: Color,
    starfishDotColor: Color,
    modifier: Modifier = Modifier
) {
    // Graph data
    val outerCircleRadiusPx = with(LocalDensity.current) { (starfishSize / 2).toPx() }
    val centeredOriginPx = PointF(outerCircleRadiusPx, outerCircleRadiusPx)
    val starfishCoordinates = StarfishCoordinates(outerCircleRadiusPx)

    // Starfish Base with Dots
    Box(modifier = modifier) {
        // Starfish Base
        StarfishBase(
            starfishSizeDp = starfishSize,
            starfishCoordinates = starfishCoordinates,
            starfishBaseColor = starfishBaseColor
        )

        // Starfish Dot Star
        StarfishDotStar(
            starfishSizeDp = starfishSize,
            centeredOriginPx = centeredOriginPx,
            starfishCoordinates = starfishCoordinates,
            starfishDotColor = starfishDotColor
        )
    }
}

/*
 * Previews
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF404040
)
@Composable
private fun StarfishPreview() {
    LavalarmTheme {
        Starfish(
            starfishSize = 300.dp,
            starfishBaseColor = StarfishBasePink,
            starfishDotColor = StarfishDotWhite
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFffe3a0
)
@Composable
private fun StarfishOnWetSandPreview() {
    LavalarmTheme {
        Starfish(
            starfishSize = 300.dp,
            starfishBaseColor = StarfishBasePink,
            starfishDotColor = StarfishDotWhite
        )
    }
}

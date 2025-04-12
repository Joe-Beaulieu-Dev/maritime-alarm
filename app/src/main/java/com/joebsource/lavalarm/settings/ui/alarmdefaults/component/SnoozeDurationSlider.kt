package com.joebsource.lavalarm.settings.ui.alarmdefaults.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joebsource.lavalarm.core.ui.theme.BoatHull
import com.joebsource.lavalarm.core.ui.theme.LavalarmTheme
import com.joebsource.lavalarm.core.ui.theme.VolcanicRock
import kotlin.math.floor
import kotlin.math.round

// Experimental OptIn for Slider
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnoozeDurationSlider(
    selectedSnoozeDuration: Int,
    updateSnoozeDuration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val minDuration = 5f
    val maxDuration = 30f

    Column(modifier = modifier) {
        // Selected Snooze Duration
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
                .width(76.dp)
                .height(76.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = VolcanicRock)
        ) {
            Text(
                text = "$selectedSnoozeDuration",
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Snooze Duration Slider and Min/Max Duration indicators
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Min Duration indicator
            Text(
                text = "${floor(minDuration).toInt()}",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Snooze Duration Slider
            Slider(
                value = selectedSnoozeDuration.toFloat(),
                // Due to what I can only assume is an internal floating-point arithmetic issue, the Slider does not always
                // provide discrete values for the Float param in the onValueChange() lambda when it should, given my configuration.
                // Due to this, the value must be rounded before converting it to an Int.
                onValueChange = { snoozeDuration -> updateSnoozeDuration(round(snoozeDuration).toInt()) },
                valueRange = minDuration..maxDuration,
                steps = 4,
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(color = BoatHull, shape = CircleShape)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            // Max Duration indicator
            Text(
                text = "${floor(maxDuration).toInt()}",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun SnoozeDurationSliderPreview() {
    LavalarmTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                SnoozeDurationSlider(
                    selectedSnoozeDuration = 15,
                    updateSnoozeDuration = {}
                )
            }
        }
    }
}

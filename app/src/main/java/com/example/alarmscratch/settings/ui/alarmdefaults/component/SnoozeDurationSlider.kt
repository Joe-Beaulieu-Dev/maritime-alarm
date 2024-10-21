package com.example.alarmscratch.settings.ui.alarmdefaults.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import kotlin.math.floor
import kotlin.math.round

@Composable
fun SnoozeDurationSlider(
    initialSnoozeDuration: Int,
    updateSnoozeDuration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var snoozeDuration by rememberSaveable { mutableFloatStateOf(initialSnoozeDuration.toFloat()) }
    val trackMovement: (Float) -> Unit = { snoozeDuration = it }
    val minDuration = 5f
    val maxDuration = 30f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Min Duration indicator
        Text(
            text = "${floor(minDuration).toInt()}",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Duration Slider
        Slider(
            value = snoozeDuration,
            onValueChange = { currentValue ->
                trackMovement(currentValue)
                updateSnoozeDuration(round(snoozeDuration).toInt())
            },
            valueRange = minDuration..maxDuration,
            steps = 4,
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

/*
 * Previews
 */

@Preview
@Composable
private fun SnoozeDurationSliderPreview() {
    AlarmScratchTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                SnoozeDurationSlider(
                    initialSnoozeDuration = 15,
                    updateSnoozeDuration = {}
                )
            }
        }
    }
}

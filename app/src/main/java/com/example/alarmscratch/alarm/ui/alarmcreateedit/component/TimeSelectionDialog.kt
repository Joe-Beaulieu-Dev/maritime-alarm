package com.example.alarmscratch.alarm.ui.alarmcreateedit.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.settings.data.model.TimeDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionDialog(
    initialHour: Int,
    initialMinute: Int,
    timeDisplay: TimeDisplay,
    onCancel: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    // State
    val is24Hour = when (timeDisplay) {
        TimeDisplay.TwelveHour ->
            false
        TimeDisplay.TwentyFourHour ->
            true
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )
    var showFullTimePicker by rememberSaveable { mutableStateOf(true) }
    // TODO: Ran into a weird layout issue with TimePicker in Landscape.
    //  Decided to move on and just lock to TimeInput in Landscape for now.
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Dialog(onDismissRequest = onCancel) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp)) {
                // Title
                Text(text = stringResource(id = R.string.select_time), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(15.dp))

                // Time selection
                if (showFullTimePicker && isPortrait) {
                    TimeSelectionPicker(timePickerState = timePickerState)
                } else {
                    TimeSelectionInput(timePickerState = timePickerState)
                }
            }

            // Bottom Button Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            ) {
                // Entry method toggle Button
                IconButton(
                    onClick = { showFullTimePicker = !showFullTimePicker },
                    enabled = isPortrait
                ) {
                    Icon(
                        imageVector = if (showFullTimePicker && isPortrait) Icons.Default.Keyboard else Icons.Default.WatchLater,
                        contentDescription = null,
                        tint = if (isPortrait) DarkerBoatSails else LightVolcanicRock
                    )
                }

                // Cancel/Confirm Button Row
                Row {
                    // Cancel Button
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(contentColor = BoatSails)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Confirm Button
                    TextButton(
                        onClick = { onConfirm(timePickerState.hour, timePickerState.minute) },
                        colors = ButtonDefaults.textButtonColors(contentColor = BoatSails)
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelectionPicker(timePickerState: TimePickerState) {
    TimePicker(
        state = timePickerState,
        colors = TimePickerDefaults.colors(
            selectorColor = LightVolcanicRock,
            clockDialColor = VolcanicRock,
            periodSelectorSelectedContainerColor = LightVolcanicRock,
            periodSelectorSelectedContentColor = BoatSails,
            periodSelectorUnselectedContentColor = LightVolcanicRock,
            timeSelectorSelectedContainerColor = LightVolcanicRock,
            timeSelectorUnselectedContainerColor = VolcanicRock
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelectionInput(timePickerState: TimePickerState) {
    TimeInput(
        state = timePickerState,
        colors = TimePickerDefaults.colors(
            selectorColor = LightVolcanicRock,
            clockDialColor = VolcanicRock,
            periodSelectorSelectedContainerColor = LightVolcanicRock,
            periodSelectorSelectedContentColor = BoatSails,
            periodSelectorUnselectedContentColor = LightVolcanicRock,
            timeSelectorSelectedContainerColor = LightVolcanicRock,
            timeSelectorUnselectedContainerColor = VolcanicRock
        )
    )
}

/*
 * Previews
 */

@Preview
@Composable
private fun TimeSelectionDialogPreview() {
    AlarmScratchTheme {
        TimeSelectionDialog(
            initialHour = 15,
            initialMinute = 45,
            timeDisplay = TimeDisplay.TwelveHour,
            onCancel = {},
            onConfirm = { _, _ -> }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimeSelectionPicker12HourPreview() {
    AlarmScratchTheme {
        TimeSelectionPicker(timePickerState = rememberTimePickerState(is24Hour = false))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimeSelectionPicker24HourPreview() {
    AlarmScratchTheme {
        TimeSelectionPicker(timePickerState = rememberTimePickerState(is24Hour = true))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimeSelectionInput12HourPreview() {
    AlarmScratchTheme {
        TimeSelectionInput(timePickerState = rememberTimePickerState(is24Hour = false))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimeSelectionInput24HourPreview() {
    AlarmScratchTheme {
        TimeSelectionInput(timePickerState = rememberTimePickerState(is24Hour = true))
    }
}

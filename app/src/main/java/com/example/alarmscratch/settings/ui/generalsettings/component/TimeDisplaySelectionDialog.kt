package com.example.alarmscratch.settings.ui.generalsettings.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.shared.BasicDialog
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.settings.data.model.TimeDisplay

@Composable
fun TimeDisplaySelectionDialog(
    initialTimeDisplay: TimeDisplay,
    onCancel: () -> Unit,
    onConfirm: (TimeDisplay) -> Unit
) {
    // State
    var selectedTimeDisplay by rememberSaveable { mutableStateOf(initialTimeDisplay) }

    // Actions
    val isTimeDisplaySelected: (TimeDisplay) -> Boolean = { it == selectedTimeDisplay }
    val updateTimeDisplay: (TimeDisplay) -> Unit = { selectedTimeDisplay = it }

    BasicDialog(
        titleRes = R.string.general_settings_time_display,
        onCancel = onCancel,
        onConfirm = { onConfirm(selectedTimeDisplay) }
    ) {
        // Time Display RadioButtons
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, top = 8.dp)
                .selectableGroup()
        ) {
            items(items = TimeDisplay.entries) { timeDisplay ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isTimeDisplaySelected(timeDisplay),
                            onClick = { updateTimeDisplay(timeDisplay) },
                            role = Role.RadioButton
                        )
                        .minimumInteractiveComponentSize()
                ) {
                    RadioButton(
                        selected = isTimeDisplaySelected(timeDisplay),
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = DarkerBoatSails,
                            unselectedColor = LightVolcanicRock
                        )
                    )
                    Text(text = timeDisplay.value, modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun TimeDisplaySelectionDialogPreview() {
    AlarmScratchTheme {
        TimeDisplaySelectionDialog(
            initialTimeDisplay = TimeDisplay.TwelveHour,
            onCancel = {},
            onConfirm = {}
        )
    }
}

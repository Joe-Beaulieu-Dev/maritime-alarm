package com.example.alarmscratch.settings.ui.generalsettings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.settings.data.model.TimeDisplay

// TODO: LocalRippleConfiguration and RippleConfiguration are Experimental APIs. Prior to these you would use
//  LocalRippleTheme and RippleTheme. However, Google deprecated LocalRippleTheme and RippleTheme with
//  DeprecationLevel.ERROR which prevents you from building. Therefore, you are forced to use Experimental APIs
//  as replacements. Keep an eye on the functionality of LocalRippleConfiguration and RippleConfiguration since
//  they are Experimental APIs.
@OptIn(ExperimentalMaterial3Api::class)
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

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkVolcanicRock),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Title
            Text(
                text = stringResource(id = R.string.general_settings_time_display),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp)
            )

            // Time Display RadioButtons
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
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
                            ),
                            modifier = Modifier.padding(start = 18.dp)
                        )
                        Text(text = timeDisplay.value, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }

            // Cancel and OK Buttons
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Replace default Ripple
                CompositionLocalProvider(value = LocalRippleConfiguration provides RippleConfiguration(color = BoatSails)) {
                    // Cancel Button
                    TextButton(onClick = onCancel) {
                        Text(text = stringResource(id = R.string.cancel), color = BoatSails)
                    }

                    // OK Button
                    TextButton(onClick = { onConfirm(selectedTimeDisplay) }) {
                        Text(text = stringResource(id = R.string.ok), color = BoatSails)
                    }
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

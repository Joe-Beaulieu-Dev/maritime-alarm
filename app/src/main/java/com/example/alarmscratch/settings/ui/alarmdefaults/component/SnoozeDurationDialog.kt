package com.example.alarmscratch.settings.ui.alarmdefaults.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock

// TODO: LocalRippleConfiguration and RippleConfiguration are Experimental APIs. Prior to these you would use
//  LocalRippleTheme and RippleTheme. However, Google deprecated LocalRippleTheme and RippleTheme with
//  DeprecationLevel.ERROR which prevents you from building. Therefore, you are forced to use Experimental APIs
//  as replacements. Keep an eye on the functionality of LocalRippleConfiguration and RippleConfiguration since
//  they are Experimental APIs.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnoozeDurationDialog(
    initialSnoozeDuration: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    // State
    var selectedSnoozeDuration by rememberSaveable { mutableIntStateOf(initialSnoozeDuration) }

    // Actions
    val updateSnoozeDuration: (Int) -> Unit = { selectedSnoozeDuration = it }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkVolcanicRock),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title
            Text(
                text = stringResource(id = R.string.snooze_duration),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 20.dp)
            )

            // Snooze Duration Slider
            SnoozeDurationSlider(
                selectedSnoozeDuration = selectedSnoozeDuration,
                updateSnoozeDuration = updateSnoozeDuration,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 18.dp)
            )

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
                    TextButton(onClick = {}) {
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
private fun SnoozeDurationDialogPreview() {
    AlarmScratchTheme {
        SnoozeDurationDialog(
            initialSnoozeDuration = 15,
            onCancel = {},
            onConfirm = {}
        )
    }
}

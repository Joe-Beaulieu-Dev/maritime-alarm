package com.example.alarmscratch.core.ui.shared

import androidx.annotation.StringRes
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

// ExperimentalMaterial3Api OptIn for LocalRippleConfiguration and RippleConfiguration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialog(
    @StringRes titleRes: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    body: @Composable () -> Unit
) {
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
                text = stringResource(id = titleRes),
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp)
            )

            // Body
            body()

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
                    TextButton(onClick = onConfirm) {
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
private fun BasicDialogPreview() {
    AlarmScratchTheme {
        BasicDialog(
            titleRes = R.string.general_settings_time_display,
            onCancel = {},
            onConfirm = {}
        ) {
            Text(
                text = "Dialog body",
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 24.dp)
            )
        }
    }
}

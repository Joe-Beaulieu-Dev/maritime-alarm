package com.example.alarmscratch.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

@Composable
fun RingtonePickerScreen(modifier: Modifier) {
    Surface(modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text(text = "Select Ringtone")
            }
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun RingtonePickerScreenPreview() {
    AlarmScratchTheme {
        RingtonePickerScreen(modifier = Modifier.fillMaxSize())
    }
}

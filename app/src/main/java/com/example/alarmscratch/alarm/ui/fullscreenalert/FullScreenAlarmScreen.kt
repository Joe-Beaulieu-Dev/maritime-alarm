package com.example.alarmscratch.alarm.ui.fullscreenalert

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme

@Composable
fun FullScreenAlarmScreen(
    alarmName: String,
    alarmTime: String
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = alarmName)
                Text(text = alarmTime)
                Button(onClick = {}) {
                    Text(text = "Dismiss Alarm")
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
private fun FullScreenAlarmScreenPreview() {
    AlarmScratchTheme {
        FullScreenAlarmScreen(
            alarmName = "Alarm Name",
            alarmTime = "Mon, 2:30 PM"
        )
    }
}

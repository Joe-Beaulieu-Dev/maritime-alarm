package com.example.alarmscratch.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.ui.theme.MediumVolcanicRock

@Composable
fun AlarmDefaultsScreen(modifier: Modifier = Modifier) {
    // TODO: Temporary code
    Surface(modifier = modifier.fillMaxSize()) {
        Column {
            Text(text = "Alarm Defaults", fontSize = 38.sp, modifier = Modifier.padding(start = 15.dp, top = 15.dp))
            SettingsComponent(icon = Icons.Default.SportsMartialArts, nameRes = R.string.settings_general, onClick = {})
            HorizontalDivider(thickness = 2.dp, color = MediumVolcanicRock, modifier = Modifier.padding(horizontal = 15.dp))
            SettingsComponent(icon = Icons.Default.Anchor, nameRes = R.string.settings_alarm_defaults, onClick = {})
        }
    }
}

@Preview
@Composable
private fun AlarmDefaultScreenPreview() {
    AlarmScratchTheme {
        AlarmDefaultsScreen()
    }
}

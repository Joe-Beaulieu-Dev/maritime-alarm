package com.example.alarmscratch.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil

@Composable
fun AlarmDefaultsScreen(modifier: Modifier = Modifier) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // TODO: Temporary code
    Surface(
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
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

package com.example.alarmscratch.settings.ui.generalsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil

@Composable
fun GeneralSettingsScreen(modifier: Modifier = Modifier) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // TODO: Temporary state
    val timeDisplay = "12hr"

    GeneralSettingsScreenContent(
        timeDisplay = timeDisplay,
        modifier = modifier
    )
}

@Composable
fun GeneralSettingsScreenContent(
    timeDisplay: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleRes = R.string.settings_general,
                navigationButton = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                },
                modifier = Modifier.background(color = MediumVolcanicRock)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ) {
            // Time Icon and Text
            Row(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = DarkerBoatSails
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.general_settings_time_label),
                    color = DarkerBoatSails,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Time Display selection
            RowSelectionItem(
                rowOnClick = {},
                rowLabelResId = R.string.general_settings_time_display,
                choiceComponent = { Text(text = timeDisplay) }
            )
        }
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun GeneralSettingsScreenPreview() {
    AlarmScratchTheme {
        GeneralSettingsScreenContent(timeDisplay = "12hr")
    }
}

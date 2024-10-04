package com.example.alarmscratch.settings.ui.generalsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.LightVolcanicRock
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.data.model.TimeDisplay

@Composable
fun GeneralSettingsScreen(modifier: Modifier = Modifier) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // TODO: Temporary state
    var timeDisplay by rememberSaveable { mutableStateOf(TimeDisplay.TwelveHour) }
    val updateTimeDisplay: (TimeDisplay) -> Unit = { timeDisplay = it }

    GeneralSettingsScreenContent(
        timeDisplay = timeDisplay,
        updateTimeDisplay = updateTimeDisplay,
        modifier = modifier
    )
}

@Composable
fun GeneralSettingsScreenContent(
    timeDisplay: TimeDisplay,
    updateTimeDisplay: (TimeDisplay) -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    var showTimeDisplaySelectionDialog by rememberSaveable { mutableStateOf(false) }
    val toggleTimeDisplaySelectionDialog: () -> Unit = { showTimeDisplaySelectionDialog = !showTimeDisplaySelectionDialog }

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
                rowOnClick = toggleTimeDisplaySelectionDialog,
                rowLabelResId = R.string.general_settings_time_display,
                choiceComponent = { Text(text = timeDisplay.value) }
            )
        }

        // Time Display Selection Dialog
        if (showTimeDisplaySelectionDialog) {
            TimeDisplaySelectionDialog(
                initialTimeDisplay = timeDisplay,
                onCancel = toggleTimeDisplaySelectionDialog,
                onConfirm = { newTimeDisplay ->
                    updateTimeDisplay(newTimeDisplay)
                    toggleTimeDisplaySelectionDialog()
                }
            )
        }
    }
}

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Title
                Text(
                    text = stringResource(id = R.string.general_settings_time_display),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp)
                )

                // Time Display Radio Buttons
                LazyColumn(
                    modifier = Modifier
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
                                modifier = Modifier.padding(start = 14.dp)
                            )
                            Text(text = timeDisplay.value, modifier = Modifier.padding(start = 12.dp))
                        }
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
private fun GeneralSettingsScreenPreview() {
    AlarmScratchTheme {
        GeneralSettingsScreenContent(
            timeDisplay = TimeDisplay.TwelveHour,
            updateTimeDisplay = {}
        )
    }
}

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

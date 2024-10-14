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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.RowSelectionItem
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil
import com.example.alarmscratch.settings.data.model.TimeDisplay
import com.example.alarmscratch.settings.data.repository.GeneralSettingsState
import com.example.alarmscratch.settings.ui.generalsettings.component.TimeDisplaySelectionDialog
import kotlinx.coroutines.launch

@Composable
fun GeneralSettingsScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    generalSettingsViewModel: GeneralSettingsViewModel = viewModel(factory = GeneralSettingsViewModel.Factory)
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val generalSettingsState by generalSettingsViewModel.modifiedGeneralSettings.collectAsState()

    if (generalSettingsState is GeneralSettingsState.Success) {
        val coroutineScope = rememberCoroutineScope()
        val generalSettings = (generalSettingsState as GeneralSettingsState.Success).generalSettings

        GeneralSettingsScreenContent(
            navHostController = navHostController,
            timeDisplay = generalSettings.timeDisplay,
            saveGeneralSettings = { coroutineScope.launch { generalSettingsViewModel.saveGeneralSettings() } },
            updateTimeDisplay = { generalSettingsViewModel.updateTimeDisplay(it) },
            modifier = modifier
        )
    }
}

@Composable
fun GeneralSettingsScreenContent(
    navHostController: NavHostController,
    timeDisplay: TimeDisplay,
    saveGeneralSettings: () -> Unit,
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
                    IconButton(onClick = navHostController::navigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(
                        onClick = {
                            saveGeneralSettings()
                            navHostController.popBackStack()
                        }
                    ) {
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

/*
 * Previews
 */

@Preview
@Composable
private fun GeneralSettingsScreenPreview() {
    AlarmScratchTheme {
        GeneralSettingsScreenContent(
            navHostController = rememberNavController(),
            timeDisplay = TimeDisplay.TwelveHour,
            saveGeneralSettings = {},
            updateTimeDisplay = {}
        )
    }
}

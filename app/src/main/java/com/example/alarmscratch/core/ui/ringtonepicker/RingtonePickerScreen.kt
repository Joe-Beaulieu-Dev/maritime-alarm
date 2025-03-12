package com.example.alarmscratch.core.ui.ringtonepicker

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.ringtoneDataSampleList
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.ui.shared.CustomTopAppBar
import com.example.alarmscratch.core.ui.shared.UnsavedChangesDialog
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.SelectedGreen
import com.example.alarmscratch.core.ui.theme.VolcanicRock
import com.example.alarmscratch.core.util.StatusBarUtil

@Composable
fun RingtonePickerScreen(
    navHostController: NavHostController,
    modifier: Modifier,
    ringtonePickerViewModel: RingtonePickerViewModel = viewModel(factory = RingtonePickerViewModel.Factory)
) {
    // Configure Status Bar
    StatusBarUtil.setDarkStatusBar()

    // State
    val ringtoneDataList = ringtonePickerViewModel.ringtoneDataList
    val selectedRingtoneUri by ringtonePickerViewModel.selectedRingtoneUri.collectAsState()
    val isRingtonePlaying by ringtonePickerViewModel.isRingtonePlaying.collectAsState()
    val showUnsavedChangesDialog by ringtonePickerViewModel.showUnsavedChangesDialog.collectAsState()
    
    RingtonePickerScreenContent(
        ringtoneDataList = ringtoneDataList,
        selectedRingtoneUri = selectedRingtoneUri,
        isRingtonePlaying = isRingtonePlaying,
        saveRingtone = { ringtonePickerViewModel.saveRingtone(navHostController) },
        selectRingtone = ringtonePickerViewModel::selectRingtone,
        tryNavigateUp = { ringtonePickerViewModel.tryNavigateUp(navHostController) },
        tryNavigateBack = { ringtonePickerViewModel.tryNavigateBack(navHostController) },
        showUnsavedChangesDialog = showUnsavedChangesDialog,
        unsavedChangesLeave = { ringtonePickerViewModel.unsavedChangesLeave(navHostController) },
        unsavedChangesStay = ringtonePickerViewModel::unsavedChangesStay,
        modifier = modifier
    )
}

@Composable
fun RingtonePickerScreenContent(
    ringtoneDataList: List<RingtoneData>,
    selectedRingtoneUri: String,
    isRingtonePlaying: Boolean,
    saveRingtone: () -> Unit,
    selectRingtone: (Context, String) -> Unit,
    tryNavigateUp: () -> Unit,
    tryNavigateBack: () -> Unit,
    showUnsavedChangesDialog: Boolean,
    unsavedChangesLeave: () -> Unit,
    unsavedChangesStay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State
    val context = LocalContext.current
    val isRowSelected: (String) -> Boolean = { it == selectedRingtoneUri }
    val isRowPlaying: (String) -> Boolean = { isRingtonePlaying && isRowSelected(it) }
    val rowColor: (String) -> Color = { if (isRowSelected(it)) VolcanicRock else DarkVolcanicRock }

    // Intercept system back navigation
    BackHandler {
        tryNavigateBack()
    }

    Surface(
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column {
            // Top App Bar
            CustomTopAppBar(
                titleRes = R.string.ringtone_picker_screen_title,
                navigationButton = {
                    IconButton(onClick = tryNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actionButton = {
                    IconButton(onClick = saveRingtone) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                },
                modifier = Modifier.background(color = MediumVolcanicRock)
            )

            // Alarm Sounds Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = DarkerBoatSails
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.ringtone_picker_alarm_sounds_label),
                    color = DarkerBoatSails,
                    fontSize = 24.sp
                )
            }

            // Ringtones
            LazyColumn(modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)) {
                items(items = ringtoneDataList) { ringtoneData ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectRingtone(context, ringtoneData.fullUri) }
                            .background(color = rowColor(ringtoneData.fullUri))
                            .padding(start = 32.dp, top = 12.dp, end = 32.dp, bottom = 12.dp)
                    ) {
                        // Ringtone Name
                        Text(text = ringtoneData.name)

                        // Ringtone playback and selection indicator Icons
                        if (isRowSelected(ringtoneData.fullUri)) {
                            Row {
                                // Ringtone playback indicator Icon
                                if (isRowPlaying(ringtoneData.fullUri)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = DarkerBoatSails
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }

                                // Selected Ringtone Icon
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SelectedGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Unsaved Changes Dialog
    if (showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onLeave = unsavedChangesLeave,
            onStay = unsavedChangesStay
        )
    }
}

/*
 * Previews
 */

@Preview
@Composable
private fun RingtonePickerScreenPlayingPreview() {
    AlarmScratchTheme {
        RingtonePickerScreenContent(
            ringtoneDataList = ringtoneDataSampleList,
            selectedRingtoneUri = sampleRingtoneData.fullUri,
            isRingtonePlaying = true,
            saveRingtone = {},
            selectRingtone = { _, _ -> },
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun RingtonePickerScreenNotPlayingPreview() {
    AlarmScratchTheme {
        RingtonePickerScreenContent(
            ringtoneDataList = ringtoneDataSampleList,
            selectedRingtoneUri = sampleRingtoneData.fullUri,
            isRingtonePlaying = false,
            saveRingtone = {},
            selectRingtone = { _, _ -> },
            tryNavigateUp = {},
            tryNavigateBack = {},
            showUnsavedChangesDialog = false,
            unsavedChangesLeave = {},
            unsavedChangesStay = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

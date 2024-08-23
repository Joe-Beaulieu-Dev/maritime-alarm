package com.example.alarmscratch.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.ringtoneDataSampleList
import com.example.alarmscratch.alarm.data.preview.sampleRingtoneData
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkVolcanicRock
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock
import com.example.alarmscratch.core.ui.theme.SelectedGreen
import com.example.alarmscratch.core.ui.theme.VolcanicRock

@Composable
fun RingtonePickerScreen(
    navHostController: NavHostController,
    modifier: Modifier,
    ringtonePickerViewModel: RingtonePickerViewModel = viewModel(factory = RingtonePickerViewModel.Factory)
) {
    // State
    val ringtoneDataList = ringtonePickerViewModel.ringtoneDataList
    val selectedRingtoneUri by ringtonePickerViewModel.selectedRingtoneUri.collectAsState()

    // Actions
    val saveRingtone: () -> Unit = {
        val alarmEditScreenSavedStateHandle: SavedStateHandle? = navHostController.previousBackStackEntry?.savedStateHandle
        ringtonePickerViewModel.saveRingtone(alarmEditScreenSavedStateHandle)
    }
    
    RingtonePickerScreenContent(
        navHostController = navHostController,
        ringtoneDataList = ringtoneDataList,
        selectedRingtoneUri = selectedRingtoneUri,
        selectRingtone = ringtonePickerViewModel::selectRingtone,
        saveRingtone = saveRingtone,
        modifier = modifier
    )
}

@Composable
fun RingtonePickerScreenContent(
    navHostController: NavHostController,
    ringtoneDataList: List<RingtoneData>,
    selectedRingtoneUri: String,
    selectRingtone: (String) -> Unit,
    saveRingtone: () -> Unit,
    modifier: Modifier
) {
    // State
    val isRowSelected: (String) -> Boolean = { it == selectedRingtoneUri }
    val rowColor: (String) -> Color = { if (isRowSelected(it)) VolcanicRock else DarkVolcanicRock }

    Surface(modifier = modifier) {
        Column {
            // Top App Bar
            RingtonePickerTopAppBar(
                navHostController = navHostController,
                saveRingtone = saveRingtone
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
                            .clickable { selectRingtone(ringtoneData.fullUriString) }
                            .background(color = rowColor(ringtoneData.fullUriString))
                            .padding(start = 32.dp, top = 12.dp, end = 32.dp, bottom = 12.dp)
                    ) {
                        // Ringtone Name
                        Text(text = ringtoneData.name)

                        // Selected Ringtone Icon
                        if (isRowSelected(ringtoneData.fullUriString)) {
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

@Composable
fun RingtonePickerTopAppBar(
    navHostController: NavHostController,
    saveRingtone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(color = MediumVolcanicRock)
            .fillMaxWidth()
    ) {
        // Up Navigation Arrow and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Up Navigation Arrow
            IconButton(onClick = { navHostController.navigateUp() }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }

            // Title
            Text(
                text = stringResource(id = R.string.ringtone_picker_screen_title),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // Save Button
        IconButton(
            onClick = {
                saveRingtone()
                navHostController.popBackStack()
            }
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
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
        RingtonePickerScreenContent(
            navHostController = rememberNavController(),
            ringtoneDataList = ringtoneDataSampleList,
            selectedRingtoneUri = sampleRingtoneData.fullUriString,
            selectRingtone = {},
            saveRingtone = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.ringtoneDataSampleList
import com.example.alarmscratch.core.data.model.RingtoneData
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.DarkerBoatSails
import com.example.alarmscratch.core.ui.theme.MediumVolcanicRock

@Composable
fun RingtonePickerScreen(
    modifier: Modifier,
    ringtonePickerViewModel: RingtonePickerViewModel = viewModel(factory = RingtonePickerViewModel.Factory)
) {
    val ringtoneDataList = ringtonePickerViewModel.ringtoneList
    
    RingtonePickerScreenContent(ringtoneDataList = ringtoneDataList, modifier = modifier)
}

@Composable
fun RingtonePickerScreenContent(
    ringtoneDataList: List<RingtoneData>,
    modifier: Modifier
) {
    Surface(modifier = modifier) {
        Column {
            // Top App Bar
            RingtonePickerTopAppBar(navigateBack = {}, saveRingtone = {})

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
            LazyColumn(
                modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 20.dp)
            ) {
                items(items = ringtoneDataList) { ringtoneData ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        Text(text = ringtoneData.name, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RingtonePickerTopAppBar(
    navigateBack: () -> Unit,
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
            IconButton(onClick = navigateBack) {
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
        IconButton(onClick = saveRingtone) {
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
            ringtoneDataList = ringtoneDataSampleList,
            modifier = Modifier.fillMaxSize()
        )
    }
}

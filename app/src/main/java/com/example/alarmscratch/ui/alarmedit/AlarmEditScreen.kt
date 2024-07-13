package com.example.alarmscratch.ui.alarmedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.alarmscratch.data.repository.AlarmState
import com.example.alarmscratch.ui.shared.AlarmCreateEditScreen
import kotlinx.coroutines.launch

@Composable
fun AlarmEditScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    alarmEditViewModel: AlarmEditViewModel = viewModel(factory = AlarmEditViewModel.Factory)
) {
    // State
    val alarmState by alarmEditViewModel.modifiedAlarm.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (alarmState is AlarmState.Success) {
        AlarmCreateEditScreen(
            navHostController = navHostController,
            alarm = (alarmState as AlarmState.Success).alarm,
            saveAlarm = { coroutineScope.launch { alarmEditViewModel.saveAlarm() } },
            updateName = alarmEditViewModel::updateName,
            updateDate = alarmEditViewModel::updateDate,
            updateTime = alarmEditViewModel::updateTime,
            addDay = alarmEditViewModel::addDay,
            removeDay = alarmEditViewModel::removeDay,
            modifier = modifier
        )
    }
}

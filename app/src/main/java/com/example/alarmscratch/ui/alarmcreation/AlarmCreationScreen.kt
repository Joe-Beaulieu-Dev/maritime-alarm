package com.example.alarmscratch.ui.alarmcreation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.alarmscratch.data.model.WeeklyRepeater
import com.example.alarmscratch.ui.shared.AlarmCreateEditScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AlarmCreationScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    alarmCreationViewModel: AlarmCreationViewModel = viewModel(factory = AlarmCreationViewModel.Factory)
) {
    // State
    val alarmState by alarmCreationViewModel.newAlarm.collectAsState()

    // Actions
    val coroutineScope = rememberCoroutineScope()
    val saveAlarm: () -> Unit = { coroutineScope.launch { alarmCreationViewModel.saveAlarm() } }
    val updateName: (String) -> Unit = alarmCreationViewModel::updateName
    val updateDate: (LocalDate) -> Unit = alarmCreationViewModel::updateDate
    val updateTime: (Int, Int) -> Unit = alarmCreationViewModel::updateTime
    val addDay: (WeeklyRepeater.Day) -> Unit = alarmCreationViewModel::addDay
    val removeDay: (WeeklyRepeater.Day) -> Unit = alarmCreationViewModel::removeDay

    AlarmCreateEditScreen(
        navHostController = navHostController,
        alarm = alarmState,
        saveAlarm = saveAlarm,
        updateName = updateName,
        updateDate = updateDate,
        updateTime = updateTime,
        addDay = addDay,
        removeDay = removeDay,
        modifier = modifier
    )
}

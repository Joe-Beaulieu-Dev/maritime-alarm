package com.example.alarmscratch.core.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.model.Alarm
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.repository.AlarmListState
import com.example.alarmscratch.core.extension.LocalDateTimeUtil
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.InCloudBlack
import com.example.alarmscratch.core.ui.theme.SkyBlue
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun SkylineHeader(
    selectedNavComponentDest: Destination,
    modifier: Modifier = Modifier,
    skylineHeaderViewModel: SkylineHeaderViewModel = viewModel(factory = SkylineHeaderViewModel.Factory)
) {
    // State
    val alarmListState by skylineHeaderViewModel.alarmList.collectAsState()

    SkylineHeaderContent(
        selectedNavComponentDest = selectedNavComponentDest,
        alarmListState = alarmListState,
        modifier = modifier
    )
}

@Composable
fun SkylineHeaderContent(
    selectedNavComponentDest: Destination,
    alarmListState: AlarmListState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Sky with Clouds and Water
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = SkyBlue)
        ) {
            // Leftmost Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(50.dp)
                    .height(30.dp)
                    .offset(x = 10.dp, y = 35.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Second from Left Small Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(30.dp)
                    .height(20.dp)
                    .offset(x = (-110).dp, y = 5.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Small part of Large Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(75.dp)
                    .height(30.dp)
                    .offset(x = (-40).dp, y = 30.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Main part of Large Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(110.dp)
                    .height(50.dp)
                    .offset(x = 0.dp, y = 5.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            ) {
                // Next Alarm Icon and Text
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    if (selectedNavComponentDest == Destination.AlarmListScreen && alarmListState is AlarmListState.Success) {
                        // TODO: Change Icon to Icons.Default.AlarmOff if there's no Active Alarms
                        // Alarm Icon
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            tint = InCloudBlack,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                        )
                        // Next Alarm Text
                        Text(
                            text = getNextAlarmText(LocalContext.current, alarmListState.alarmList),
                            fontWeight = FontWeight.SemiBold,
                            color = InCloudBlack
                        )
                    }
                }
            }

            // Sun
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(50.dp)
                    .height(50.dp)
                    .offset(x = (-20).dp, y = 0.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Yellow)
            )

            // Sun Cloud
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(90.dp)
                    .height(30.dp)
                    .offset(x = (-10).dp, y = 20.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.White)
            )

            // Boat
            SailBoat(
                boatSize = 30.dp,
                hullColor = BoatHull,
                sailColor = BoatSails,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 65.dp, y = 1.dp)
            )
        }
    }
}

// TODO: Handle java exceptions
private fun getNextAlarmText(context: Context, alarmList: List<Alarm>): String =
    getNextAlarm(alarmList)
        ?.let { nextAlarm ->
            val secondsTillNextAlarm = LocalDateTime.now().until(nextAlarm.dateTime, ChronoUnit.SECONDS).toDouble()

            // Days
            val days = floor(secondsTillNextAlarm / 86400)
            val remainderAfterDays = secondsTillNextAlarm - days * 86400
            // Hours
            val hours = floor(remainderAfterDays / 3600)
            val remainderAfterHours = remainderAfterDays - hours * 3600
            // Minutes - round up because we're not displaying seconds
            val minutes = ceil(remainderAfterHours / 60)

            // Days
            (if (days >= 1) "${days.toInt()}${context.getString(R.string.day_abbreviation)}" else "") +
                    // Space - only needed if we have both Days and Hours
                    (if (days >= 1 && hours >= 1) " " else "") +
                    // Hours
                    (if (hours >= 1) "${hours.toInt()}${context.getString(R.string.hour_abbreviation)}" else "") +
                    // Space - only needed if we have both Hours and Minutes
                    (if (hours >= 1 && minutes >= 1) " " else "") +
                    // Minutes
                    (if (minutes >= 1) "${minutes.toInt()}${context.getString(R.string.minute_abbreviation)}" else "")
        } ?: context.getString(R.string.no_active_alarms)

private fun getNextAlarm(alarmList: List<Alarm>): Alarm? =
    alarmList
        .filter { it.enabled && it.dateTime.isAfter(LocalDateTimeUtil.nowTruncated()) }
        .minByOrNull { it.dateTime }

/*
 * Preview
 */

@Preview
@Composable
private fun SkylineHeaderAlarmListScreenPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.AlarmListScreen,
            alarmListState = AlarmListState.Success(alarmList = listOf(consistentFutureAlarm))
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderAlarmListScreenNoAlarmsPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.AlarmListScreen,
            alarmListState = AlarmListState.Success(alarmList = emptyList())
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSettingsScreenPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.SettingsScreen,
            alarmListState = AlarmListState.Success(alarmList = listOf(consistentFutureAlarm))
        )
    }
}

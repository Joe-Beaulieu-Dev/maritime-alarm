package com.example.alarmscratch.core.ui.core.component

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
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmscratch.R
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.snoozedAlarm
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.extension.isSnoozed
import com.example.alarmscratch.core.extension.toCountdownString
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.shared.SailBoat
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.InCloudBlack
import com.example.alarmscratch.core.ui.theme.SkyBlue

@Composable
fun SkylineHeader(
    selectedNavComponentDest: Destination,
    modifier: Modifier = Modifier,
    skylineHeaderViewModel: SkylineHeaderViewModel = viewModel(factory = SkylineHeaderViewModel.Factory)
) {
    // State
    val nextAlarmState by skylineHeaderViewModel.nextAlarm.collectAsState()

    SkylineHeaderContent(
        selectedNavComponentDest = selectedNavComponentDest,
        nextAlarmState = nextAlarmState,
        modifier = modifier
    )
}

@Composable
fun SkylineHeaderContent(
    selectedNavComponentDest: Destination,
    nextAlarmState: AlarmState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Sky with Clouds and Boat
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
                    if (selectedNavComponentDest == Destination.AlarmListScreen) {
                        // Next Alarm Info
                        val alarmIcon =
                            if (nextAlarmState is AlarmState.Success) {
                                if (nextAlarmState.alarm.isSnoozed()) {
                                    Icons.Default.Snooze
                                } else {
                                    Icons.Default.Alarm
                                }
                            } else {
                                Icons.Default.AlarmOff
                            }
                        val countdownText =
                            if (nextAlarmState is AlarmState.Success) {
                                nextAlarmState.alarm.toCountdownString(LocalContext.current)
                            } else {
                                stringResource(id = R.string.no_active_alarms)
                            }

                        // Alarm Icon
                        Icon(
                            imageVector = alarmIcon,
                            tint = InCloudBlack,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                        )
                        // Countdown Text
                        Text(
                            text = countdownText,
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

/*
 * Preview
 */

@Preview
@Composable
private fun SkylineHeaderStandardAlarmPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.AlarmListScreen,
            nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSnoozedAlarmPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.AlarmListScreen,
            nextAlarmState = AlarmState.Success(alarm = snoozedAlarm)
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderNoAlarmsPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.AlarmListScreen,
            nextAlarmState = AlarmState.Error(Throwable())
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSettingsScreenPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            selectedNavComponentDest = Destination.SettingsScreen,
            nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
        )
    }
}

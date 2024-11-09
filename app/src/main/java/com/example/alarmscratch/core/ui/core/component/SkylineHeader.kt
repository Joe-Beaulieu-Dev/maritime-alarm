package com.example.alarmscratch.core.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarmscratch.alarm.data.preview.consistentFutureAlarm
import com.example.alarmscratch.alarm.data.preview.snoozedAlarm
import com.example.alarmscratch.alarm.data.repository.AlarmState
import com.example.alarmscratch.core.navigation.Destination
import com.example.alarmscratch.core.ui.shared.SailBoat
import com.example.alarmscratch.core.ui.theme.AlarmScratchTheme
import com.example.alarmscratch.core.ui.theme.BoatHull
import com.example.alarmscratch.core.ui.theme.BoatSails
import com.example.alarmscratch.core.ui.theme.SkyBlue
import java.time.LocalDateTime

@Composable
fun SkylineHeader(
    selectedNavComponentDest: Destination,
    modifier: Modifier = Modifier
) {
    SkylineHeaderContent(
        nextAlarmIndicator = { NextAlarmCloud(selectedNavComponentDest = selectedNavComponentDest) },
        modifier = modifier
    )
}

@Composable
fun SkylineHeaderContent(
    nextAlarmIndicator: @Composable () -> Unit,
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

            // Next Alarm Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = 0.dp, y = 5.dp)
            ) {
                // Small part of Cloud
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .width(75.dp)
                        .height(30.dp)
                        .offset(x = (-22).dp, y = 5.dp)
                        .clip(shape = CircleShape)
                        .background(color = Color.White)
                )

                // Large part of Cloud
                nextAlarmIndicator()
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
private fun SkylineHeaderOneLineAlarmPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloudContent(
                    selectedNavComponentDest = Destination.AlarmListScreen,
                    nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderTwoLineAlarmPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloudContent(
                    selectedNavComponentDest = Destination.AlarmListScreen,
                    nextAlarmState = AlarmState.Success(
                        alarm = consistentFutureAlarm.copy(
                            dateTime = LocalDateTime.now().plusDays(12).plusHours(10).plusMinutes(45)
                        )
                    )
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSnoozedAlarmPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloudContent(
                    selectedNavComponentDest = Destination.AlarmListScreen,
                    nextAlarmState = AlarmState.Success(alarm = snoozedAlarm)
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderNoAlarmsPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloudContent(
                    selectedNavComponentDest = Destination.AlarmListScreen,
                    nextAlarmState = AlarmState.Error(Throwable())
                )
            }
        )
    }
}

@Preview
@Composable
private fun SkylineHeaderSettingsScreenPreview() {
    AlarmScratchTheme {
        SkylineHeaderContent(
            nextAlarmIndicator = {
                NextAlarmCloudContent(
                    selectedNavComponentDest = Destination.SettingsScreen,
                    nextAlarmState = AlarmState.Success(alarm = consistentFutureAlarm)
                )
            }
        )
    }
}
